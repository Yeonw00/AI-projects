import { useEffect, useRef, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { apiFetch } from "../api/client";

function ChargeSuccess() {
    const { search } = useLocation();
    const nav = useNavigate();
    const qs = new URLSearchParams(search);
    const paymentKey = qs.get("paymentKey");
    const orderId = qs.get("orderId");
    const amount = Number(qs.get("amount"));

    const [status, setStatus] = useState("processing");
    const [errorCode, setErrorCode] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const [coins, setCoins] = useState(null);
    const calledRef = useRef(false);

    useEffect(() => {
        if (calledRef.current) return;
            calledRef.current = true;
        (async () => {
            try {
                if (!paymentKey || !orderId || !Number.isFinite(amount))  {
                    throw new Error("잘못된 접근입니다.");
                }

                const res = await apiFetch("/api/payments/confirm", {
                    method: "POST",
                    body: JSON.stringify({ paymentKey, orderId, amount }),
                });

                setStatus("done");
                setCoins(res?.coinsAdded ?? 0);
            } catch (e) {
                console.error("결제 승인 실패:", e);
                const status = e?.status || e?.response?.status;
                const msg = e?.message || e?.response?.data?.message;
                setErrorCode(status ?? "UNKNOWN");
                setErrorMessage(msg ?? "결제 승인에 실패했습니다.");
                setStatus("error");
                const httpStatus = e?.status || e?.response?.status;
                if (httpStatus === 401 || httpStatus === 403) {
                    setTimeout(() => nav("/login", { replace: true, state: { from: "/charge/success" } }), 1500);
                }
            }

        })();
    }, [paymentKey, orderId, amount, nav]);

    if (status === "processing") {
        return <div style={{ padding: 24 }}>결제 승인 중...</div>
    }

    if (status === "error") {
        return (
            <div style={{ padding: 24, color: "#b30000"}}>
                <h2>결제 실패</h2>
                {errorCode && <p>에러 코드: {errorCode}</p>}
                <p>{errorMessage}</p>
                <button onClick={() => nav("/charge")} style={{ marginTop: 12 }}>
                    다시 시도하기
                </button>
            </div>
        );
    }

    return (
        <div style={{ padding: 24 }}>
            <h2>결제 성공</h2>
            <p>코인 {coins ?? 0}개가 충전되었습니다.</p>
            <button onClick={() => nav("/")}>홈으로</button>
        </div>
    );
}
export default ChargeSuccess;