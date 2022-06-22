package com.alibaba.nls.client.protocol.dm;

import com.alibaba.nls.client.protocol.SpeechResProtocol;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author zhishen.ml
 * @date 2017/11/24
 * <p>
 * 唤醒服务返回结果
 */
public class DialogAssistantResponse extends SpeechResProtocol {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 检测到的关键词，null表示没有监测到
     *
     * @return
     */
    public Boolean getAccepted() {
        return (Boolean) payload.get("accepted");
    }

    /**
     * 置信度.(语音识别结果的置信度，范围0~1)
     *
     * @return
     */
    public Double getConfidence() {
        if (payload.get("confidence") == null) {
            return 0.0;
        }
        return Double.parseDouble(payload.get("confidence").toString());
    }

    /**
     * 语音识别结果
     *
     * @return
     */
    public String getAsrResult() {
        return (String) payload.get("result");
    }

    /**
     * 对话上下文信息
     *
     * @return
     */
    public String getActionContext() {
        return (String) payload.get("action_context");
    }

    /**
     * 对话结果中用于显示的文本
     *
     * @return
     */
    public String getDisplayText() {
        return (String) payload.get("display_text");
    }

    /**
     * 对话结果中用于语音播报的文本
     *
     * @return
     */
    public String getSpokenText() {
        return (String) payload.get("spoken_text");
    }

    /**
     * 对话触发的动作
     *
     * @return
     */
    public String getAction() {
        return (String) payload.get("action");
    }

    /**
     * 动作相关参数
     *
     * @return
     */
    public List<Object> getActionParams() throws IOException {
        String params = (String) payload.get("action_params");
        if (params != null) {
            JsonNode jsonNode = OBJECT_MAPPER.readTree(params);
            List<Object> list = new ArrayList<>();
            for (JsonNode node : jsonNode) {
                list.add(node);
            }
            return list;
        } else {
            return null;
        }
    }
}