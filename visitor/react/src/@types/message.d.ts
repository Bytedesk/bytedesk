// @ts-ignore
/* eslint-disable */

declare namespace MESSAGE {
  //
  type Message = {
    mid?: string;
    timestamp?: string;
    client?: string;
    version?: string;
    type?: string;
    status?: string;
    user?: VISITOR_AI.Visitor;
    text?: Text;
    image?: Image;
    voice?: voice;
    video?: video;
    file?: file;
    content?: string;
    thread?: THREAD.Thread;
  };

  type MessageDTO = {
    id?: string;
    mid?: string;
    wid?: string;
    cid?: string;
    gid?: string;
    localId?: string;
    type?: string;
    sessionType?: string;
    client?: string;
    content?: string;
    picUrl?: string;
    imageUrl?: string;
    fileUrl?: string;
    fileName?: string;
    fileSize?: string;
    mediaId?: string;
    format?: string;
    voiceUrl?: string;
    length?: number;
    played?: boolean;
    destroyAfterReading?: boolean;
    destroyAfterLength?: number;
    thumbMediaId?: string;
    videoOrShortUrl?: string;
    videoOrShortThumbUrl?: string;
    locationX?: number;
    locationY?: number;
    scale?: number;
    label?: string;
    title?: string;
    description?: string;
    url?: string;
    status?: string;
    rate?: number;
    responseSeconds?: number;
    thread?: THREAD.Thread;
    user?: VISITOR_AI.Visitor;
    answers?: Answer[];
    answer?: Answer;
    categories?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type PageResultMessage = {
    message?: string;
    status_code?: number;
    data?: MessageDTO;
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
    query?: string; //
    knowledge_base_name?: string;
    top_k?: 3;
    score_threshold?: 1;
    history?: [];
    stream?: true;
    model_name?: "zhipu-api";
    temperature?: number;
    prompt_name?: "knowledge_base_chat";
    local_doc_url?: boolean;
  };
}
