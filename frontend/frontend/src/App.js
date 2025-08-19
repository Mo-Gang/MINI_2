import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import ProtectedRoute from "./components/ProtectedRoute";

function LoggedOutOnly({ children }) {
    const token = localStorage.getItem("token");
    return token ? <Navigate to="/dashboard" replace /> : children;
}

export default function App() {
    return (
        <BrowserRouter>
            <Routes>
                {/* 기본 진입은 대시보드 → ProtectedRoute에서 미로그인 시 /login 으로 이동 */}
                <Route path="/" element={<Navigate to="/dashboard" replace />} />

                <Route
                    path="/login"
                    element={
                        <LoggedOutOnly>
                            <Login />
                        </LoggedOutOnly>
                    }
                />

                <Route
                    path="/dashboard"
                    element={
                        <ProtectedRoute>
                            <Dashboard />
                        </ProtectedRoute>
                    }
                />

                {/* 404 */}
                <Route path="*" element={<div style={{padding:24}}>페이지를 찾을 수 없어요. <a href="/dashboard">대시보드</a></div>} />
            </Routes>
        </BrowserRouter>
    );
}
