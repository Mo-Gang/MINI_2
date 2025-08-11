import "./App.css";

import React, { useEffect, useMemo, useRef, useState } from "react";

const API = "http://localhost:8080/sensor/status";

// 등급
const LEVEL = { NORMAL: 0, WARN: 1, DANGER: 2 };
const levelName = (lv) => (lv===2?'위험':lv===1?'경고':'정상');
const levelChipStyle = (lv) => ({
    display: "inline-block", padding: "2px 8px", borderRadius: 999,
    fontWeight: 600, color: "#fff",
    background: lv===2 ? "#ef4444" : lv===1 ? "#f59e0b" : "#10b981"
});

// ── 단건 임계 ─────────────────────────────────────────────────────────────
const gasLevel = (ppm) => ppm >= 2000 ? LEVEL.DANGER : ppm >= 1000 ? LEVEL.WARN : LEVEL.NORMAL;
const humidityLevel = (h) => h >= 75 ? LEVEL.DANGER : h >= 65 ? LEVEL.WARN : LEVEL.NORMAL;
const tempHumidLevel = (t, h) => (t >= 40 && h >= 80) ? LEVEL.DANGER : (t >= 30 && t <= 35) ? LEVEL.WARN : LEVEL.NORMAL;

// 최종 등급(가장 높은 위험도)
const worst = (...lvs) => lvs.reduce((a,b)=>Math.max(a,b), LEVEL.NORMAL);

// ── 슬라이딩 윈도우 유틸 ────────────────────────────────────────────────
// buf: [{t, ok}] 최근 샘플들 저장. windowMs보다 오래된 건 삭제.
// ok=true인 구간이 끊기지 않고 windowMs 이상 지속되면 true
function pushAndCheck(buf, ok, now, windowMs) {
    buf.current.push({ t: now, ok });
    const from = now - windowMs;
    // 윈도우에서 벗어난 오래된 샘플 제거
    while (buf.current.length && buf.current[0].t < from) buf.current.shift();

    // 최근부터 거꾸로 올라가면서 ok가 끊기지 않은 연속구간 길이 계산
    let lastT = now, dur = 0;
    for (let i = buf.current.length - 1; i >= 0; i--) {
        const { t, ok: v } = buf.current[i];
        if (!v) break;
        dur += (lastT - t);
        lastT = t;
        if (dur >= windowMs) return true;
    }
    return false;
}

export default function App() {
    const [form, setForm] = useState({ gas: 710, sound: 95, distance: 18, temp: 36.2, humidity: 82, flame: 1 });
    const [result, setResult] = useState(null);
    const [auto, setAuto] = useState(true);
    const [period, setPeriod] = useState(2000);
    const [sending, setSending] = useState(false);
    const abortRef = useRef(null);

    // 버퍼: 최근 샘플 저장
    const soundBuf = useRef([]);   // ok: sound >= 100
    const distBuf  = useRef([]);   // ok: distance <= 15
    const flameBuf = useRef([]);   // ok: flame == 1

    const [holdFlags, setHoldFlags] = useState({ soundDanger:false, distDanger:false, flameDanger:false });

    const bodyJson = useMemo(() => JSON.stringify(form), [form]);

    const sendOnce = async () => {
        try {
            setSending(true);
            abortRef.current?.abort();
            const ctrl = new AbortController();
            abortRef.current = ctrl;

            const res = await fetch(API, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: bodyJson,
                signal: ctrl.signal
            });
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            const data = await res.json();
            setResult(data);
        } catch (e) {
            if (e.name !== "AbortError") setResult({ status: "ERR", message: String(e) });
        } finally {
            setSending(false);
        }

        // 샘플을 보낸 시점(now) 기준으로 버퍼 업데이트
        const now = Date.now();
        const sustainedSound = pushAndCheck(soundBuf, form.sound >= 100, now, 2000); // 2s
        const sustainedDist  = pushAndCheck(distBuf,  form.distance <= 15,  now, 2000); // 2s
        const sustainedFlame = pushAndCheck(flameBuf, !!form.flame,         now, 1000); // 1s
        setHoldFlags({ soundDanger: sustainedSound, distDanger: sustainedDist, flameDanger: sustainedFlame });
    };

    useEffect(() => { sendOnce(); }, []);
    useEffect(() => {
        if (!auto) return;
        const id = setInterval(sendOnce, Math.max(300, Number(period) || 2000));
        return () => clearInterval(id);
    }, [auto, period, bodyJson]);

    const onChange = (k) => (e) => {
        const v = e.target.value;
        setForm((prev) => ({
            ...prev,
            [k]: (k==="temp"||k==="humidity") ? Number(v) : parseInt(v || 0, 10)
        }));
    };

    // 등급 계산(슬라이딩 윈도우 반영)
    // 소리: >=100dB 2s 이상 → 위험, 아니면 단건 기준
    const soundLvInstant = form.sound >= 100 ? LEVEL.DANGER : form.sound >= 85 ? LEVEL.WARN : LEVEL.NORMAL;
    const soundLv = holdFlags.soundDanger ? LEVEL.DANGER : soundLvInstant;

    // 거리: <=15cm 2s 이상 → 위험, 아니면 단건 기준
    const distLvInstant = form.distance <= 15 ? LEVEL.DANGER : form.distance <= 30 ? LEVEL.WARN : LEVEL.NORMAL;
    const distLv = holdFlags.distDanger ? LEVEL.DANGER : distLvInstant;

    // 불꽃: 감지 1s 이상 유지 → 위험 (즉시 위험으로 바꾸고 싶으면 아래 한 줄만 LEVEL.DANGER로)
    const flameLv = holdFlags.flameDanger ? LEVEL.DANGER : (form.flame ? LEVEL.WARN : LEVEL.NORMAL);

    const lvGas   = gasLevel(form.gas);
    const lvHumid = humidityLevel(form.humidity);
    const lvTempH = tempHumidLevel(form.temp, form.humidity);
    const overall = worst(soundLv, distLv, flameLv, lvGas, lvHumid, lvTempH);

    return (
        <div style={{ padding: 20, fontFamily: "system-ui, sans-serif", lineHeight: 1.5 }}>
            <h1>실시간 대응형 공장 안전 모니터링 시스템</h1>

            <div style={{ display: "grid", gridTemplateColumns: "120px 1fr", gap: 8, maxWidth: 620 }}>
                {["gas","sound","distance","temp","humidity","flame"].map((k) => (
                    <React.Fragment key={k}>
                        <label style={{ alignSelf: "center" }}>{k}</label>
                        <input
                            type="number"
                            step={k==="temp"||k==="humidity" ? "0.1" : "1"}
                            value={form[k]}
                            onChange={onChange(k)}
                            style={{ padding: 8, borderRadius: 8, border: "1px solid #ccc" }}
                        />
                    </React.Fragment>
                ))}
            </div>

            <div style={{ marginTop: 12, display: "flex", gap: 12, alignItems: "center", flexWrap: "wrap" }}>
                <button onClick={sendOnce} disabled={sending} style={{ padding: "8px 14px", borderRadius: 8 }}>
                    {sending ? "전송 중..." : "지금 한 번 전송"}
                </button>
                <label><input type="checkbox" checked={auto} onChange={(e)=>setAuto(e.target.checked)}/> 자동 전송</label>
                <label>주기(ms) <input type="number" value={period} onChange={(e)=>setPeriod(e.target.value)} style={{ width: 100, padding: 6, borderRadius: 8, border: "1px solid #ccc" }}/></label>
                <span style={{ ...levelChipStyle(overall) }}>전체: {levelName(overall)}</span>
            </div>

            <h3 style={{ marginTop: 16 }}>센서별 등급</h3>
            <ul style={{ listStyle: "none", padding: 0, display: "grid", gridTemplateColumns: "repeat(auto-fit,minmax(220px,1fr))", gap: 12 }}>
                <li><span style={levelChipStyle(lvGas)}>가스: {levelName(lvGas)}</span><div>경고 ≥1000ppm, 위험 ≥2000ppm</div></li>
                <li><span style={levelChipStyle(soundLv)}>소리: {levelName(soundLv)}</span><div>경고 ≥85dB, 위험 ≥100dB(연속 2s)</div></li>
                <li><span style={levelChipStyle(distLv)}>거리: {levelName(distLv)}</span><div>경고 ≤30cm, 위험 ≤15cm(연속 2s)</div></li>
                <li><span style={levelChipStyle(lvHumid)}>습도: {levelName(lvHumid)}</span><div>정상 40~60%, 경고 65~75%, 위험 ≥75%</div></li>
                <li><span style={levelChipStyle(lvTempH)}>온도·습도: {levelName(lvTempH)}</span><div>경고 30~35℃, 위험 ≥40℃ &amp; 습도≥80%</div></li>
                <li><span style={levelChipStyle(flameLv)}>불꽃: {levelName(flameLv)}</span><div>감지 1s 유지 시 위험</div></li>
            </ul>

            <h3>백엔드 응답</h3>
            <pre style={{ background: "#f6f8fa", padding: 12, borderRadius: 8 }}>
        {result ? JSON.stringify(result, null, 2) : "대기 중..."}
      </pre>

            <h3>전송 JSON</h3>
            <pre style={{ background: "#f6f8fa", padding: 12, borderRadius: 8 }}>
        {JSON.stringify(form, null, 2)}
      </pre>
        </div>
    );
}
