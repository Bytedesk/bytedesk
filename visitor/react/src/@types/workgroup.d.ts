// @ts-ignore
/* eslint-disable */

declare namespace WORKGROUP {
  type WorkGroup = {
    createdAt?: string;
    updatedAt?: null;
    id?: number;
    wid?: string;
    nickname?: string;
    avatar?: string;
    defaultRobot?: boolean;
    offlineRobot?: boolean;
    nonWorktimeRobot?: boolean;
    welcomeTip?: string;
    showRobotFaq?: false;
    showContact?: false;
    showContactMobile?: false;
    showContactWechatQrcodeUrl?: false;
    acceptTip?: string;
    reRequestTip?: string;
    nonWorkingTimeTip?: string;
    robotTip?: string;
    offlineTip?: string;
    closeTip?: string;
    autoCloseTip?: string;
    forceRate?: boolean;
    routeType?: string;
    leaveMessageType?: string;
    recent?: boolean;
    defaulted?: boolean;
    department?: boolean;
    autoPop?: boolean;
    showTopTip?: boolean;
    topTip?: string;
    showForm?: boolean;
    showCollectMobile?: boolean;
    robotCollectMobile?: boolean;
    hideLogo?: boolean;
    popAfterTimeLength?: number;
    maxQueueCount?: number;
    maxQueueCountExceedTip?: string;
    maxQueueSecond?: number;
    maxQueueSecondExceedTip?: string;
    robotAnswerNotFoundTip?: string;
    showRobotFaqNotFound?: boolean;
    showRightColumn?: boolean;
    slogan?: string;
    about?: string;
    description?: string;
    webhook?: string;
    webhookLeaveMessage?: string;
    deleted?: boolean;
    autoCloseMinutes?: number;
    questionnaire?: string;
    onDutyWorkGroup?: string;
    robot?: ROBOT.Profile;
    workCode?: WorkCode;
    workTimes?: WorkTime[];
    workTabs?: WorkTab[];
    users?: VISITOR_AI.Visitor[];
    admin?: null;
    workTime?: true;
  };

  type HttpResultWorkGroup = {
    message?: string;
    status_code?: number;
    data?: WorkGroup;
  };

  type WorkCode = {
    createdAt?: null;
    updatedAt?: null;
    id?: 2308;
    wid?: "202303021033021";
    type?: "workgroup";
    lang?: "cn";
    shape?: "round";
    buttonText?: "在线客服";
    buttonColor?: "#ffffff";
    buttonBackground?: "#FF4800";
    bannerColor?: "#ffffff";
    bannerBackground?: "#FF8C00";
    position?: "right";
    rightMargin?: "50";
    leftMargin?: "50";
    bottomMargin?: "50";
    positionWindow?: "right";
    column?: "one";
    history?: "0";
    v2robot?: "0";
    published?: false;
  };

  type WorkTab = {
    createdAt?: null;
    updatedAt?: null;
    id?: 1778;
    wid?: "202302231800031";
    title?: "问题测试";
    content?: "";
    type?: "faq";
    position?: "right";
    orderNo?: 0;
    published?: true;
  };

  type WorkTime = {
    createdAt?: null;
    updatedAt?: null;
    id?: 4058;
    startTime?: "08?:00?:00";
    endTime?: "23?:00?:00";
    workTime?: true;
  };
}
