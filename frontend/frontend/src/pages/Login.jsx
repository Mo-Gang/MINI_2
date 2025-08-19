import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../api";

export default function Login() {
    const nav = useNavigate();
    const [form, setForm] = useState({ username: "", password: "" });
    const [err, setErr] = useState("");

    useEffect(() => {
        console.log("[MOUNT] Login.jsx mounted from:", import.meta?.env?.BASE_URL);
        console.log("[BOOT] api.baseURL =", api?.defaults?.baseURL);
    }, []);

    const onChange = (e) =>
        setForm({ ...form, [e.target.name]: e.target.value });

    const onSubmit = async (e) => {
        e?.preventDefault?.();
        console.log("[LOGIN] fired", form);
        setErr("");

        try {
            const res = await api.post("/api/auth/login", form, {
                validateStatus: () => true,
            });
            console.log("[LOGIN_RES]", res.status, res.data);

            if (res.status === 200) {
                const token =
                    typeof res.data === "string" ? res.data : res.data?.token;
                if (!token) throw new Error("서버 응답에 token이 없습니다.");
                localStorage.setItem("token", token);
                nav("/dashboard");
                return;
            }

            const msg =
                res.data?.message ||
                (typeof res.data === "string" ? res.data : "") ||
                `코드 ${res.status}`;
            setErr(`로그인 실패: ${msg}`);
        } catch (e) {
            console.error("LOGIN_FAIL ▶", e?.response?.status, e?.response?.data, e.message);
            setErr(`로그인 실패: ${e?.response?.data?.message || e.message}`);
        }
    };

    // 🔹 버튼을 submit이 아닌 button으로! (기본 폼 제출 완전 차단)
    const onClickLogin = (e) => {
        e.preventDefault();
        onSubmit(e);
    };

    return (
        <div style={{ minHeight: "100vh", display: "grid", placeItems: "center", background: "#f7f7f8" }}>
            <form onSubmit={onSubmit} noValidate style={{ width: 340, background: "#fff", padding: 24, borderRadius: 16, boxShadow: "0 8px 30px rgba(0,0,0,.06)" }}>
                <h1 style={{ marginBottom: 16, fontSize: 20, fontWeight: 700 }}>로그인</h1>

                <label style={{ display: "block", fontSize: 12, color: "#666" }}>아이디</label>
                <input
                    name="username"
                    value={form.username}
                    onChange={onChange}
                    autoComplete="username"
                    style={{ width: "100%", padding: 10, border: "1px solid #ddd", borderRadius: 10, margin: "6px 0 12px" }}
                />

                <label style={{ display: "block", fontSize: 12, color: "#666" }}>비밀번호</label>
                <input
                    type="password"
                    name="password"
                    value={form.password}
                    onChange={onChange}
                    autoComplete="current-password"
                    style={{ width: "100%", padding: 10, border: "1px solid #ddd", borderRadius: 10, margin: "6px 0 16px" }}
                />

                {err && <div style={{ color: "#dc2626", fontSize: 12, marginBottom: 10 }}>{err}</div>}

                {/* 기본 제출 막기 위해 type="button" + onClick */}
                <button type="button" onClick={onClickLogin}
                        style={{ width: "100%", padding: 10, borderRadius: 10, background: "#111", color: "#fff" }}>
                    로그인
                </button>
            </form>
        </div>
    );
}
