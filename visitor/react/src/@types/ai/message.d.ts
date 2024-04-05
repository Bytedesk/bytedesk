// @ts-ignore
/* eslint-disable */

declare namespace MESSAGE_AI {
  //
  type Message = {
    mid?: string;
    created_at?: string;
    client?: string;
    type?: string;
    status?: string;
    user?: VISITOR_AI.Visitor;
    content?: string;
    thread?: THREAD.Thread;
  };

  type HttpResultMessage = {
    message?: string;
    status_code?: number;
    data?: Message;
  };

  type Text = {
    content?: string;
  };

  type Image = {
    imageUrl?: string;
  };

  type voice = {
    voiceUrl?: string;
    length?: string;
    format?: string;
  };

  type video = {
    videoOrShortUrl?: string;
  };

  type file = {
    fileUrl?: string;
  };

  type commodidy = {
    id?: string;
    title?: string;
    content?: string;
    price?: string;
    url?: string;
    imageUrl?: string;
    imageUrl?: string;
    type?: string;
  };

  type Channel = {
    id: string;
    cid: string;
    nickname: string;
    avatar: string;
    topic: string;
    type: string;
    description: string;
    createdAt: string;
    updatedAt: string;
  };

  type Answer = {
    aid: string;
    question: string;
    answer: string;
  };

  //
  type knowledge_base_chat = {
    query?: string;
    rid?: string;
  };
}
