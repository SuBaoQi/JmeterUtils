package com.jmeter.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author Bob
 * @title: Test
 * @projectName JmeterUtils
 * @description: TODO
 * @date 2021/5/1814:46
 */
public class Test
{
    public static void main(String[] args)
    {
        String response = "\n" +
                "{\n" +
                "    \"applyNo\": \"DEAJ202132041000000002\",\n" +
                "    \"bizType\": \"1\",\n" +
                "    \"importNo\": \"IMEAJ0000202000021\",\n" +
                "    \"importVos\": [\n" +
                "        {\n" +
                "            \"prpCinsuredIdvList\": [\n" +
                "                {\n" +
                "                    \"birthday\": \"1997-06-02 00:00:00\",\n" +
                "                    \"insuredFlag\": \"010000000000000000000000000000\",\n" +
                "                    \"flag\": \"修改\",\n" +
                "                    \"identifyNumber\": \"210103198506020034\",\n" +
                "                    \"occupationCode\": \"020103\",\n" +
                "                    \"identifyType\": \"01\",\n" +
                "                    \"groupNo\": \"1\",\n" +
                "                    \"insuredCName\": \"接口测试批改团单导入一\",\n" +
                "\t\t\t\t\t\"sex\":\"1\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"flag\": \"I\",\n" +
                "            \"importSerialNo\": \"\",\n" +
                "            \"orderId\": \"1\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"prpCinsuredIdvList\": [\n" +
                "                {\n" +
                "                    \"birthday\": \"1986-06-02 00:00:00\",\n" +
                "                    \"insuredFlag\": \"010000000000000000000000000000\",\n" +
                "                    \"flag\": \"增加\",\n" +
                "                    \"identifyNumber\": \"210103198506020050\",\n" +
                "                    \"occupationCode\": \"040202\",\n" +
                "                    \"identifyType\": \"01\",\n" +
                "                    \"groupNo\": \"1\",\n" +
                "                    \"insuredCName\": \"接口测试批改团单导入二\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"flag\": \"I\",\n" +
                "            \"importSerialNo\": \"\",\n" +
                "            \"orderId\": \"2\"\n" +
                "        },\n" +
                "\t\t        {\n" +
                "            \"prpCinsuredIdvList\": [\n" +
                "                {\n" +
                "                    \"birthday\": \"1932-06-02 00:00:00\",\n" +
                "                    \"insuredFlag\": \"010000000000000000000000000000\",\n" +
                "                    \"flag\": \"增加\",\n" +
                "                    \"identifyNumber\": \"210103198506020077\",\n" +
                "                    \"occupationCode\": \"010103\",\n" +
                "                    \"identifyType\": \"01\",\n" +
                "                    \"groupNo\": \"1\",\n" +
                "                    \"insuredCName\": \"接口测试批改团单导入三\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"flag\": \"I\",\n" +
                "            \"importSerialNo\": \"\",\n" +
                "            \"orderId\": \"3\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"policyType\": \"03\",\n" +
                "    \"serialNo\": \"156\"\n" +
                "}\n" +
                "\n";
        CompareUtil compareUtil = new CompareUtil();
        JSONObject jsonObject1 = JSONObject.parseObject(response);
        JSONObject jsonObject = CompareUtil.dealData(jsonObject1);
        System.out.println(jsonObject);
/*        JSONArray objects = new JSONArray();
        compareUtil.getJAByField("prpCinsuredIdvList",jsonObject1,objects);
        System.out.println(objects);*/

    }
}
