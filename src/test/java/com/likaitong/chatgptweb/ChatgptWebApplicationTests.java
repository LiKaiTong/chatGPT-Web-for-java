package com.likaitong.chatgptweb;

import com.alibaba.fastjson.JSON;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;
import com.likaitong.chatgptweb.entity.ChatGPTEntity.Message;
import com.likaitong.chatgptweb.entity.ResponseEntity.ResponsewithDialogMessage;
import com.sun.jmx.snmp.SnmpCounter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.transform.Source;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class ChatgptWebApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void tokenTest(){
        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        Encoding enc = registry.getEncoding(EncodingType.CL100K_BASE);
        String s="在深度学习中，视频理解任务（Video Understanding）是指对视频数据进行分析和理解的任务，主要涉及对视频中运动、动作、语义等方面的理解和识别。 常见的视频理解任务包括： 1. 动作识别（Action Recognition）：通过对视频数据进行分析，实现对其中的动作类型进行识别。动作识别技术可应用于视频监控、体育赛事直播等领域。 2. 视频分类（Video Classification）：将视频数据分类到特定的类别中，如电影、广告、短片等。视频分类技术可用于社交网络、在线视频网站等领域。 3. 目标跟踪（Object Tracking）：通过对视频数据进行分析，实现对目标对象在多个连续帧中运动轨迹的跟踪。目标跟踪技术可应用于智能交通、视频监控等领域。 4. 姿态估计（Pose Estimation）：通过对视频数据进行分析，实现对人物或物体的姿态、动作等特征的识别。姿态估计技术可用于体育训练、健身等领域。 在深度学习中，主要使用了各种卷积神经网络、循环神经网络等模型进行视频理解任务的处理和分析。其中，一些经典的深度学习模型，如卷积神经网络（CNN）、循环神经网络（RNN）等，也可以进行视频数据预测、填充、生成等高级任务研究。";
        System.out.println(enc.countTokens(s));

    }

    @Test
    void jsonTest(){
        Message m1=new Message("user","hello");
        List<Message> messageList=new ArrayList<>();
        messageList.add(m1);
        ResponsewithDialogMessage rdm=new ResponsewithDialogMessage(200,messageList);
        System.out.println(JSON.toJSONString(rdm));
    }

}
