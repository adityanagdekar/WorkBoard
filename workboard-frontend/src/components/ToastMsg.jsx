import "../style/ToastMsg.css";
import { X } from "lucide-react";

const ToastMsg = ({ toasts, removeToast }) => {
  return (
    <div className="ToastContainer">
      {toasts.map((toast, idx) => (
        <div key={idx} className={`Toast ${toast.type}`}>
          <div className="ToastContent">
            <span>{toast.message}</span>
            <X className="ToastClose" onClick={() => removeToast(idx)} />
          </div>
        </div>
      ))}
    </div>
  );
};
export default ToastMsg;
