import { useEffect } from "react";
import BoardBtn from "./BoardBtn";
import "../style/Modal.css";
const Modal = ({
  modalMsg,
  modalYesOnClick,
  modalNoOnClick,
  onBackdropClick,
  disabledChk,
}) => {
  return (
    <div className="ModalOverlay" onClick={onBackdropClick}>
      <div className="Modal">
        <div className="ModalContent">
          <p>{modalMsg}</p>
        </div>
        <div className="ModalFooter">
          <div className="ModalBtnContainer">
            <BoardBtn
              label="Yes"
              variant="modal-yes"
              onClick={modalYesOnClick}
              disabled={disabledChk}
            />
            <BoardBtn label="No" variant="modal-no" onClick={modalNoOnClick} />
          </div>
        </div>
      </div>
    </div>
  );
};
export default Modal;
