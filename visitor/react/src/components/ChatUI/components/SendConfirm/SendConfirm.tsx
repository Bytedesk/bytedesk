import React, { useState, useEffect } from "react";
import { Modal } from "../Modal";
import { Flex } from "../Flex";
import { useLocale } from "../ConfigProvider";

export type SendConfirmProps = {
  file: Blob;
  onCancel: () => void;
  onSend: () => void;
};

export const SendConfirm = (props: SendConfirmProps) => {
  const { file, onCancel, onSend } = props;
  const [img, setImg] = useState("");
  const { trans } = useLocale("SendConfirm");

  useEffect(() => {
    const reader = new FileReader();
    reader.onload = (e: ProgressEvent<FileReader>) => {
      if (e.target) {
        setImg(e.target.result as string);
      }
    };
    reader.readAsDataURL(file);
  }, [file]);

  return (
    <Modal
      className="SendConfirm"
      title={trans("title")}
      active={!!img}
      vertical={false}
      actions={[
        {
          label: trans("cancel"),
          onClick: onCancel,
        },
        {
          label: trans("send"),
          color: "primary",
          onClick: onSend,
        },
      ]}
    >
      <Flex className="SendConfirm-inner" center>
        <img src={img} alt="" />
      </Flex>
    </Modal>
  );
};
