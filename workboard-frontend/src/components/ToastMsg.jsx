import "../style/ToastMsg.css";
import { X } from "lucide-react";

const ToastMsg = ({ toasts, removeToast }) => {
  return (
    <div className="ToastContainer">
      {toasts.map((toast) => (
        <div key={toast.id} className={`Toast ${toast.type}`}>
          <div className="ToastContent">
            <span>{toast.message}</span>
            <X className="ToastClose" onClick={() => removeToast(toast.id)} />
          </div>
        </div>
      ))}
    </div>
  );
};
export default ToastMsg;
