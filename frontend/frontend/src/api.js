// src/api.js
import axios from 'axios';

export const api = axios.create({
    baseURL: '/',                    // 도메인 없이 경로만! -> CRA proxy가 8088로 포워딩
    headers: { 'Content-Type': 'application/json' }
});

// 디버깅 로그 (그대로 둬요)
api.interceptors.request.use((c)=>{
    console.log("[REQ]", c.method, (c.baseURL||"") + (c.url||""), c.data);
    return c;
});
api.interceptors.response.use(
    (r)=>{ console.log("[RES]", r.status, r.data); return r; },
    (e)=>{ console.log("[ERR]", e?.message, e?.response?.status, e?.response?.data); return Promise.reject(e); }
);
