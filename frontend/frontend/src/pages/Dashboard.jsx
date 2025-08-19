// src/pages/Dashboard.jsx
import React, { useEffect, useMemo, useState } from "react";
import { BellRing, AlertTriangle } from "lucide-react";
import {
    ResponsiveContainer,
    LineChart,
    Line,
    XAxis,
    YAxis,
    Tooltip,
    Legend,
} from "recharts";

// 연속값 센서(차트/실시간 카드에 표시)
const SENSORS = [
    { key: "temperature", label: "온도", unit: "°C" },
    { key: "humidity", label: "습도", unit: "%" },
    { key: "distance", label: "거리", unit: "cm" },
];

const fmtTime = (t) => new Date(t).toLocaleTimeString();
const clamp = (v, min, max) => Math.max(min, Math.min(max, v));

export default function Dashboard() {
    // ---- 데모 라이브 데이터 (실데이터는 WebSocket/SSE로 교체) ----
    const [live, setLive] = useState([]);
    useEffect(() => {
        const id = setInterval(() => {
            const now = Date.now();
            setLive((prev) => [
                ...prev.slice(-239),
                {
                    ts: now,
                    temperature: 24 + Math.random() * 8,
                    humidity: 50 + Math.random() * 20,
                    distance: clamp(20 + Math.random() * 10, 0, 100),
                    // 불꽃/연기 감지는 데모로 랜덤 binary
                    flame: Math.random() < 0.05, // 5% 확률
                    smoke: Math.random() < 0.06, // 6% 확률
                },
            ]);
        }, 1000);
        return () => clearInterval(id);
    }, []);

    const latest = live[live.length - 1];

    // ---- 임계값(연속값) ----
    const [th, setTh] = useState({ temperature: 35, humidity: 75, distance: 3 });

    // ---- 알림(연속값 위주) ----
    const alerts = useMemo(() => {
        const out = [];
        for (let i = Math.max(0, live.length - 60); i < live.length; i++) {
            const r = live[i];
            if (r?.temperature >= th.temperature)
                out.push({ ts: r.ts, sensor: "온도", value: r.temperature, type: "경고" });
            if (r?.humidity >= th.humidity)
                out.push({ ts: r.ts, sensor: "습도", value: r.humidity, type: "경고" });
            if (r?.distance < th.distance)
                out.push({ ts: r.ts, sensor: "거리", value: r.distance, type: "위험" });
            if (r?.flame)
                out.push({ ts: r.ts, sensor: "불꽃", value: 1, type: "위험" });
            if (r?.smoke)
                out.push({ ts: r.ts, sensor: "연기", value: 1, type: "경고" });
        }
        return out.slice(-15).reverse();
    }, [live, th]);

    return (
        <div style={{ minHeight: "100vh", background: "#f7f7f8" }}>
            {/* 헤더 */}
            <div
                style={{
                    position: "sticky",
                    top: 0,
                    zIndex: 10,
                    background: "rgba(255,255,255,0.9)",
                    backdropFilter: "blur(6px)",
                    borderBottom: "1px solid #eee",
                }}
            >
                <div
                    style={{
                        maxWidth: 1100,
                        margin: "0 auto",
                        padding: "12px 16px",
                        display: "flex",
                        gap: 8,
                        alignItems: "center",
                    }}
                >
                    <strong>실시간 대응형 공장 안전 모니터링 시스템</strong>
                    <button
                        onClick={() => {
                            localStorage.removeItem("token");
                            window.location.href = "/login";
                        }}
                        style={{
                            marginLeft: "auto",
                            padding: "6px 10px",
                            borderRadius: 8,
                            background: "#e5e7eb",
                        }}
                    >
                        로그아웃
                    </button>
                </div>
            </div>

            <main style={{ maxWidth: 1100, margin: "0 auto", padding: 16 }}>
                {/* 실시간 카드(연속값) */}
                <section>
                    <div
                        style={{
                            display: "grid",
                            gridTemplateColumns: "repeat(3,1fr)",
                            gap: 12,
                        }}
                    >
                        {SENSORS.map(({ key, label, unit }) => (
                            <div
                                key={key}
                                style={{
                                    background: "#fff",
                                    borderRadius: 16,
                                    padding: 12,
                                    boxShadow: "0 1px 3px rgba(0,0,0,.06)",
                                }}
                            >
                                <div style={{ fontSize: 12, color: "#666" }}>{label}</div>
                                <div style={{ fontSize: 22, fontWeight: 700 }}>
                                    {latest?.[key] != null ? latest[key].toFixed(1) : "-"}{" "}
                                    <span style={{ fontSize: 12, color: "#666" }}>{unit}</span>
                                </div>
                            </div>
                        ))}
                    </div>
                </section>

                {/* 이산값(불꽃/연기) 카드 */}
                <section style={{ marginTop: 12 }}>
                    <div
                        style={{
                            display: "grid",
                            gridTemplateColumns: "repeat(2,1fr)",
                            gap: 12,
                        }}
                    >
                        {/* 불꽃 */}
                        <div
                            style={{
                                background: "#fff",
                                borderRadius: 16,
                                padding: 12,
                                boxShadow: "0 1px 3px rgba(0,0,0,.06)",
                            }}
                        >
                            <div style={{ fontSize: 12, color: "#666" }}>불꽃(Flame)</div>
                            <div
                                style={{
                                    fontSize: 22,
                                    fontWeight: 700,
                                    color: latest?.flame ? "#dc2626" : "#16a34a",
                                }}
                            >
                                {latest?.flame ? "감지됨" : "정상"}
                            </div>
                        </div>

                        {/* 연기 */}
                        <div
                            style={{
                                background: "#fff",
                                borderRadius: 16,
                                padding: 12,
                                boxShadow: "0 1px 3px rgba(0,0,0,.06)",
                            }}
                        >
                            <div style={{ fontSize: 12, color: "#666" }}>연기(Smoke)</div>
                            <div
                                style={{
                                    fontSize: 22,
                                    fontWeight: 700,
                                    color: latest?.smoke ? "#f59e0b" : "#16a34a",
                                }}
                            >
                                {latest?.smoke ? "감지됨" : "정상"}
                            </div>
                        </div>
                    </div>
                </section>

                {/* 트렌드 차트 */}
                <section style={{ marginTop: 20 }}>
                    <h3 style={{ margin: "8px 0" }}>실시간 트렌드</h3>
                    <div style={{ background: "#fff", borderRadius: 16, padding: 12 }}>
                        <div style={{ height: 280 }}>
                            <ResponsiveContainer width="100%" height="100%">
                                <LineChart data={live}>
                                    <XAxis dataKey="ts" tickFormatter={fmtTime} minTickGap={40} />
                                    <YAxis width={42} />
                                    <Tooltip labelFormatter={fmtTime} />
                                    <Legend />
                                    <Line dataKey="temperature" name="온도" dot={false} />
                                    <Line dataKey="humidity" name="습도" dot={false} />
                                    <Line dataKey="distance" name="거리" dot={false} />
                                </LineChart>
                            </ResponsiveContainer>
                        </div>
                    </div>
                </section>

                {/* 알림 */}
                <section style={{ marginTop: 20 }}>
                    <h3 style={{ margin: "8px 0", display: "flex", alignItems: "center", gap: 6 }}>
                        <BellRing size={18} /> 알림
                    </h3>
                    {alerts.length === 0 ? (
                        <div style={{ background: "#fff", borderRadius: 16, padding: 16, color: "#666" }}>
                            최근 알림 없음
                        </div>
                    ) : (
                        <div style={{ display: "grid", gap: 8 }}>
                            {alerts.map((a, i) => (
                                <div
                                    key={i}
                                    style={{
                                        background: "#fff",
                                        borderRadius: 16,
                                        padding: 12,
                                        borderLeft: "4px solid #f59e0b",
                                        display: "flex",
                                        gap: 8,
                                        alignItems: "center",
                                    }}
                                >
                                    <AlertTriangle size={16} color="#b45309" />
                                    <strong>{a.type}</strong>
                                    <span>· {a.sensor}</span>
                                    <span style={{ color: "#666" }}>· {fmtTime(a.ts)}</span>
                                    <span style={{ marginLeft: "auto" }}>
                    {a.sensor === "불꽃" || a.sensor === "연기" ? "감지됨" : `값: ${a.value?.toFixed?.(1)}`}
                  </span>
                                </div>
                            ))}
                        </div>
                    )}
                </section>

                {/* 임계값 설정 */}
                <section style={{ marginTop: 20 }}>
                    <h3 style={{ margin: "8px 0" }}>임계값 설정</h3>
                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: 12 }}>
                        {Object.entries(th).map(([k, v]) => (
                            <div key={k} style={{ background: "#fff", borderRadius: 16, padding: 12 }}>
                                <div style={{ fontWeight: 600, marginBottom: 8 }}>{k}</div>
                                <input
                                    type="range"
                                    min={k === "distance" ? 0 : 0}
                                    max={k === "distance" ? 50 : k === "humidity" ? 100 : 60}
                                    step={1}
                                    value={v}
                                    onChange={(e) => setTh((p) => ({ ...p, [k]: Number(e.target.value) }))}
                                    style={{ width: "100%" }}
                                />
                                <input
                                    type="number"
                                    value={v}
                                    onChange={(e) => setTh((p) => ({ ...p, [k]: Number(e.target.value) }))}
                                    style={{ marginTop: 8, width: 100, padding: 6, border: "1px solid #ddd", borderRadius: 8 }}
                                />
                            </div>
                        ))}
                    </div>
                </section>

                {/* 안내문(불꽃/연기) */}
                <section style={{ marginTop: 20 }}>
                    <div style={{ background: "#fff", borderRadius: 16, padding: 12 }}>
                        <div style={{ fontWeight: 600, marginBottom: 6 }}>참고</div>
                        <div style={{ color: "#666", fontSize: 14 }}>
                            불꽃/연기는 값 자체가 아니라 <b>감지 시 즉시</b> 알림을 발생합니다(임계값 설정 대상 아님).
                            연속 센서(온도/습도/거리)는 임계값을 넘거나 미만일 때 알림이 뜹니다.
                        </div>
                    </div>
                </section>
            </main>

            <footer style={{ borderTop: "1px solid #eee", padding: 16, fontSize: 12, color: "#666" }}>
                © {new Date().getFullYear()} SmartFactory Team
            </footer>
        </div>
    );
}
