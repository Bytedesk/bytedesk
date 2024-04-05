import React from "react";
import useStompClient from "../hooks/useStompClient";

function withStompClient<P>(WrappedComponent: React.ComponentType<P>) {
  return (props: P) => {
    const stompClient = useStompClient();
    return <WrappedComponent stompClient={stompClient} {...props} />;
  };
}

export default withStompClient;
