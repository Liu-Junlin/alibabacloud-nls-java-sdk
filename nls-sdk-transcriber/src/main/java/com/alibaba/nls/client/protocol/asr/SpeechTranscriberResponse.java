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

package com.alibaba.nls.client.protocol.asr;

import com.alibaba.nls.client.protocol.SpeechResProtocol;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author zhishen.ml
 * @date 2018/05/24
 * <p>
 * 长语音的识别结果
 */
public class SpeechTranscriberResponse extends SpeechResProtocol {
    private static final Logger logger = LoggerFactory.getLogger(SpeechTranscriberListener.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static class Word {
        /**
         * 识别结果
         */
        public String text;
        /**
         * 词语在断句中的开始时间 单位毫秒
         */
        public Long startTime;
        /**
         * 词语在断句中的结束时间 单位毫秒
         */
        public Long endTime;

        @Override
        public String toString() {
            try {
                return OBJECT_MAPPER.writeValueAsString(this);
            } catch (JsonProcessingException e) {
                logger.error("", e);
            }
            return "{}";
        }
    }

    /**
     * 句子的index
     *
     * @return
     */
    public Integer getTransSentenceIndex() {
        return (Integer) payload.get("index");
    }

    /**
     * 当前已处理的音频时长，单位是毫秒
     *
     * @return
     */
    public Integer getTransSentenceTime() {
        return (Integer) payload.get("time");
    }

    /**
     * 结果置信度,0.0-1.0 值越大置信度越高
     *
     * @return
     */
    public Double getConfidence() {
        Object o = payload.get("confidence");
        if (o != null) {
            return Double.parseDouble(o.toString());
        }
        return null;
    }

    /**
     * sentenceBegin事件对应的时间
     *
     * @return
     */
    public Integer getSentenceBeginTime() {
        return (Integer) payload.get("begin_time");
    }


    /**
     * 最终识别结果
     *
     * @return
     */
    public String getTransSentenceText() {
        return (String) payload.get("result");
    }

    /**
     * 获取分词的词语.仅当enable_words=true时有效
     *
     * @return
     */
    public List<Word> getWords() throws IOException {
        String words = (String) payload.get("words");
        if (words != null) {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, Word.class);
            return OBJECT_MAPPER.readValue(words, javaType);
        }
        return null;
    }
}
