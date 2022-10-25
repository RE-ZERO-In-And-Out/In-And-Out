import styled from "styled-components";

const Modal = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
`;

const ModalBody = styled.div`
  position: absolute;
  width: 800px;
  height: 800px;
  padding: 40px;
  text-align: center;
  background-color: gray;
  border-radius: 10px;
  box-shadow: 0 2px 3px 0 rgba(34, 36, 38, 0.15);
`;

const ModalCloseBtn = styled.div`
  position: absolute;
  top: 15px;
  right: 15px;
  border: none;
  color: rgba(0, 0, 0, 0.7);
  background-color: transparent;
  font-size: 20px;

  &:hover {
    cursor: pointer;
  }
`;

function DiaryModal(props) {
  function closeModal() {
    props.closeModal();
  }

  return (
    <Modal onClick={closeModal}>
      <ModalBody onClick={(e) => e.stopPropagation()}>
        <ModalCloseBtn onClick={closeModal}>✖</ModalCloseBtn>
        {props.children}
      </ModalBody>
    </Modal>
  );
}

export default DiaryModal;