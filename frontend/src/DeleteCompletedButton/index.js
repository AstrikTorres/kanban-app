import React from "react";

function DeleteCompletedButton(props) {
  const onClickButton = () => {
    props.deleteCompletedTodos();
  }
  
  return (
    <button
      className="button"
      onClick={onClickButton}
      style={{
        width: props.whidth,
        height: props.height,
        background: "red",
        marginTop: "10px",
      }}
    >
      Delete completed todos
    </button>
  );
}

export { DeleteCompletedButton };