import React, { useState } from 'react';

function SensorStatus() {
    const [result, setResult] = useState(null);

    const testSensorData = {
        gas: 710,
        sound: 95,
        distance: 18,
        temp: 36.2,
        humidity: 82.0,
        flame: 1
    };

    const fetchSensorStatus = async () => {
        const response = await fetch("http://localhost:8080/api/sensor", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(testSensorData)
        });

        const data = await response.json();
        setResult(data);
    };

    return (
        <div>
            <h2>센서 상태 확인</h2>
            <button onClick={fetchSensorStatus}>센서 상태 가져오기</button>

            {result && (
                <div style={{ marginTop: '20px' }}>
                    <h3>
                        센서 상태:{" "}
                        <span style={{
                            color:
                                result.status === '위험'
                                    ? 'red'
                                    : result.status === '경고'
                                        ? 'orange'
                                        : 'green',
                            fontWeight: 'bold'
                        }}>
              {result.status}
            </span>
                    </h3>
                    <ul>
                        {result.reasons.map((r, i) => (
                            <li key={i}>{r}</li>
                        ))}
                    </ul>
                </div>
            )}
        </div>
    );
}

export default SensorStatus;
