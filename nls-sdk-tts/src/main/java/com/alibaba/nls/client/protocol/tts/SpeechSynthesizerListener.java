/*
 * Copyright 2015 Alibaba Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.nls.client.protocol.tts;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

import com.alibaba.nls.client.protocol.Constant;
import com.alibaba.nls.client.transport.ConnectionListener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhishen.ml
 * @date 2017/11/28
 *
 */
public abstract class SpeechSynthesizerListener implements ConnectionListener {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    Logger logger = LoggerFactory.getLogger(SpeechSynthesizerListener.class);
    private CountDownLatch completeLatch;
    private CountDownLatch readyLatch;

    private SpeechSynthesizer speechSynthesizer;

    void setSpeechSynthesizer(SpeechSynthesizer speechSynthesizer){
        this.speechSynthesizer=speechSynthesizer;
    }

    /**
     * 语音合成结束
     *
     * @param response
     */
    abstract public void onComplete(SpeechSynthesizerResponse response);

    public void onMetaInfo(SpeechSynthesizerResponse response){
        logger.info("MetaInfo event:{}",response.getTaskId());
    }

    @Override
    public void onOpen() {
        logger.info("connection is ok");

    }

    @Override
    public void onClose(int closeCode, String reason) {
        if(speechSynthesizer!=null){
            speechSynthesizer.markClosed();
        }
        logger.info("connection is closed due to {},code:{}", reason, closeCode);

    }


    @Override
    public void onMessage(String message) throws IOException {
        if (message == null || message.trim().length() == 0) {
            return;
        }
        logger.debug("on message:{}", message);
        SpeechSynthesizerResponse response = OBJECT_MAPPER.readValue(message, SpeechSynthesizerResponse.class);
        if (isComplete(response)) {
            onComplete(response);
            completeLatch.countDown();
        } else if (isTaskFailed(response)) {
            onFail(response);
            completeLatch.countDown();
        } else if (isMetaInfo(response)){
            onMetaInfo(response);
        } else {
            logger.warn(message);
        }

    }

    /**
     * 失败处理
     * @param response
     */
    abstract public  void onFail(SpeechSynthesizerResponse response) ;

    @Override
    abstract public void onMessage(ByteBuffer message);


    private boolean isComplete(SpeechSynthesizerResponse response) {
        String name = response.getName();
        if (name.equals(TTSConstant.VALUE_NAME_TTS_COMPLETE)) {
            return true;
        }
        return false;
    }

    private boolean isTaskFailed(SpeechSynthesizerResponse response) {
        String name = response.getName();
        if (name.equals(Constant.VALUE_NAME_TASK_FAILE)) {
            return true;
        }
        return false;
    }

    private boolean isMetaInfo(SpeechSynthesizerResponse response) {
        String name = response.getName();
        if (name.equals("MetaInfo")) {
            return true;
        }
        return false;
    }

    public void setCompleteLatch(CountDownLatch latch) {
        completeLatch = latch;
    }

}
