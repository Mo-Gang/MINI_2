import React, { useState } from "react";

const API = "http://localhost:8080";

export default function SensorStatus() {
    const [result, setResult] = useState(null);
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState("");

    // 테스트용 센서 값 (나중에 실제값으로 대체)
    const sample = { gas: 710, sound: 95, distance: 18, temp: 36.2, humidity: 82, flame: 1 };

    const fetchStatus = async () => {
        try {
            setLoading(true); setErr("");
            const res = await fetch(`${API}/sensor/status`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(sample)
            });
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            const data = await res.json(); // {status, message} 형태
            setResult(data);
        } catch (e) {
            setErr(String(e));
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{maxWidth:600}}>
            <h2>센서 상태 확인</h2>
            <button onClick={fetchStatus}>센서 상태 가져오기</button>

            {loading && <p>불러오는 중...</p>}
            {err && <p style={{color:"crimson"}}>에러: {err}</p>}

            {result && (
                <div style={{marginTop:16, padding:12, border:"1px solid #ddd"}}>
                    <b>현재 상태: </b>
                    <span style={{color: result.status==="위험"?"red":result.status==="경고"?"orange":"green"}}>
            {result.status}
          </span>
                    {result.message && <p>사유: {result.message}</p>}
                </div>
            )}
        </div>
    );
}
