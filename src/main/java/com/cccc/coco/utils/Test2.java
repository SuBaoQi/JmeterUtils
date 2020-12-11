package com.cccc.coco.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Bob
 * @create 2020-12-03 9:32
 */
public class Test2
{
    /**
     * 通用的 通过json路径找到json值
     * @param jsonObject 要取值的json对象
     * @param path 对象路径
     * @return 对象值列表 由于可能存在A.B.C路径中B为列表的情况，所以结果可能有多个
     */
    public static List<Object> getJsonFieldValue(JSONObject jsonObject, String path) {
        List<String> keyWordList = new ArrayList(Arrays.asList(path.split("\\.")));
        List<Object> list = new ArrayList<>();
        String key = keyWordList.get(0);
        Object object = jsonObject.get(key);
        keyWordList.remove(0);
        if (keyWordList.isEmpty()) {
            if (null != object) {
                list.add(object);
            }
            return list;
        }

        String subPath = StringUtils.join(keyWordList, ".");
        if (object instanceof JSONArray) {
            JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(object));
            if (jsonArray.isEmpty()) {
                return new ArrayList<>();
            }
            jsonArray.forEach(e -> list.addAll(getJsonFieldValue(JSON.parseObject(JSON.toJSONString(e)), subPath)));
        }else if(object instanceof JSONObject){
            JSONObject subJsonObject = JSONObject.parseObject(JSON.toJSONString(object));
            list.addAll(getJsonFieldValue(JSON.parseObject(JSON.toJSONString(subJsonObject)), subPath));
        }
        return list;
    }

    public static void main(String[] args)
    {
        String str ="{\n" +
                "    \"status\":0,\n" +
                "    \"statusText\":\"Success\",\n" +
                "    \"data\":{\n" +
                "        \"message\":null,\n" +
                "        \"errorCode\":null,\n" +
                "        \"applyNo\":null,\n" +
                "        \"prpPhead\":{\n" +
                "            \"id\":null,\n" +
                "            \"endorseNo\":null,\n" +
                "            \"policyNo\":\"PZIP202033016000000038\",\n" +
                "            \"classCode\":null,\n" +
                "            \"riskCode\":\"ZIP\",\n" +
                "            \"endorseTimes\":null,\n" +
                "            \"makeCom\":null,\n" +
                "            \"compensateNo\":null,\n" +
                "            \"insuredCode\":null,\n" +
                "            \"insuredName\":null,\n" +
                "            \"language\":null,\n" +
                "            \"policyType\":null,\n" +
                "            \"reasonCode\":\"01\",\n" +
                "            \"reasonText\":null,\n" +
                "            \"endorType\":null,\n" +
                "            \"endorDate\":\"2020-11-26 00:00:00\",\n" +
                "            \"validDate\":\"2020-12-01 00:00:00\",\n" +
                "            \"validHour\":null,\n" +
                "            \"handlerCode\":null,\n" +
                "            \"handler1Code\":null,\n" +
                "            \"approverCode\":null,\n" +
                "            \"checkFlag\":null,\n" +
                "            \"checkUpCode\":null,\n" +
                "            \"checkOpinion\":null,\n" +
                "            \"underWriteCode\":null,\n" +
                "            \"underWriteName\":null,\n" +
                "            \"operatorCode\":null,\n" +
                "            \"inputTime\":null,\n" +
                "            \"comCode\":null,\n" +
                "            \"agentCode\":null,\n" +
                "            \"statisticsYM\":null,\n" +
                "            \"underWriteEndDate\":null,\n" +
                "            \"underWriteFlag\":null,\n" +
                "            \"jfeeFlag\":null,\n" +
                "            \"dmFlag\":null,\n" +
                "            \"inputFlag\":null,\n" +
                "            \"flag\":null,\n" +
                "            \"insertTimeForHis\":null,\n" +
                "            \"operateTimeForHis\":null,\n" +
                "            \"tkey\":null,\n" +
                "            \"refundRate\":null,\n" +
                "            \"prpPmainAccs\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPmainExts\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPmainBonds\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPmainCargos\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPitemDevices\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPitems\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPspecialFacs\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPcoinsDetails\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPinsuredIdvLists\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPitemCargos\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPagentDetails\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPshipDrivers\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPbatchs\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPitemShips\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPcoinses\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPitemPlanes\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPmainAirLines\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPmainLiabs\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPpayeeAccounts\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPfees\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPcommissions\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPitemHouses\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPitemTravelAgencys\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPprofitFactors\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPplans\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPmainCredits\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPrations\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPmainAgris\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPnames\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPagents\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPitemCars\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPinsureds\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPitemProps\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPinsuredNatures\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPitemKinds\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPitemKindDetails\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPmains\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPcoeffs\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPengages\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPmainConstructs\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPmainLoans\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPinsuredArtifs\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPlimits\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPaddresses\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPmainProps\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPextendInfos\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPcontractPauses\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPtexts\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPcargoDetails\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPcommissionDetails\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPclauses\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPmainCommons\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPclauseplans\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPitemKindTaxFees\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPitemConstructs\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPinsuredCreditInvests\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPitemCreditoths\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpPinsuredCataLists\":[\n" +
                "\n" +
                "            ]\n" +
                "        },\n" +
                "        \"PolicyMainOld\":{\n" +
                "            \"proposalNo\":\"TZIP202033016000000090\",\n" +
                "            \"policyNo\":\"PZIP202033016000000038\",\n" +
                "            \"classCode\":\"10\",\n" +
                "            \"riskCode\":\"ZIP\",\n" +
                "            \"riskName\":null,\n" +
                "            \"projectCode\":\"\",\n" +
                "            \"contractNo\":\"88888888\",\n" +
                "            \"policySort\":\"1\",\n" +
                "            \"businessNature\":\"0\",\n" +
                "            \"language\":\"C\",\n" +
                "            \"policyType\":\"01\",\n" +
                "            \"agriFlag\":\"0\",\n" +
                "            \"operateDate\":\"2020-11-25 19:18:51\",\n" +
                "            \"startDate\":\"2020-11-26 19:18:51\",\n" +
                "            \"endDate\":\"2021-11-25 19:18:51\",\n" +
                "            \"startHour\":19,\n" +
                "            \"endHour\":19,\n" +
                "            \"disRate\":null,\n" +
                "            \"sumValue\":303,\n" +
                "            \"sumAmount\":3270,\n" +
                "            \"sumDiscount\":null,\n" +
                "            \"sumPremiumB4Discount\":null,\n" +
                "            \"couponAmount\":null,\n" +
                "            \"couponPremium\":null,\n" +
                "            \"minPremium\":null,\n" +
                "            \"sumPremium\":1,\n" +
                "            \"sumSubPrem\":0,\n" +
                "            \"sumQuantity\":1,\n" +
                "            \"policyCount\":1,\n" +
                "            \"judicalScope\":\"01\",\n" +
                "            \"argueSolution\":\"2\",\n" +
                "            \"arbitBoardName\":\"评伦霉惦舔\",\n" +
                "            \"payTimes\":1,\n" +
                "            \"makeCom\":\"33010400\",\n" +
                "            \"operateSite\":\"寨势苑财戍\",\n" +
                "            \"comCode\":\"33010400\",\n" +
                "            \"handlerCode\":\"18251488\",\n" +
                "            \"handler1Code\":\"18251488\",\n" +
                "            \"checkFlag\":\"4\",\n" +
                "            \"checkUpCode\":\"\",\n" +
                "            \"checkOpinion\":\"\",\n" +
                "            \"underWriteCode\":\"A320097680\",\n" +
                "            \"underWriteName\":\"江苏核保岗\",\n" +
                "            \"operatorCode\":\"83298873\",\n" +
                "            \"inputTime\":\"2020-11-25 19:18:51\",\n" +
                "            \"underWriteEndDate\":\"2020-11-25\",\n" +
                "            \"statisticsYM\":\"202011\",\n" +
                "            \"agentCode\":\"000002000001\",\n" +
                "            \"coinsFlag\":\"00\",\n" +
                "            \"reinsFlag\":\"0000000000\",\n" +
                "            \"allinsFlag\":\"0\",\n" +
                "            \"underWriteFlag\":\"1\",\n" +
                "            \"jfeeFlag\":\"1\",\n" +
                "            \"inputFlag\":\"0\",\n" +
                "            \"undwrtSubmitDate\":\"2020-11-25\",\n" +
                "            \"othFlag\":\"000000YY00\",\n" +
                "            \"remark\":\"接口自动化测试_10001407\",\n" +
                "            \"checkCode\":\"\",\n" +
                "            \"flag\":\"         A\",\n" +
                "            \"insertTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "            \"operateTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "            \"payMode\":\"01\",\n" +
                "            \"payCode\":null,\n" +
                "            \"crossFlag\":\"0\",\n" +
                "            \"batchGroupNo\":null,\n" +
                "            \"sumTaxFee\":0.06,\n" +
                "            \"sumNetPremium\":0.94,\n" +
                "            \"prePremium\":null,\n" +
                "            \"pkey\":\"PZIP202033016000000038\",\n" +
                "            \"tkey\":\"2020-11-25 22:12:55\",\n" +
                "            \"currency\":\"CNY\",\n" +
                "            \"dmFlag\":\"A\",\n" +
                "            \"handler1Code_uni\":\"1233134176\",\n" +
                "            \"handlerCode_uni\":\"1233134176\",\n" +
                "            \"isAutoePolicy\":\"0\",\n" +
                "            \"salesCode\":\"83298873\",\n" +
                "            \"personOri\":\"0\",\n" +
                "            \"productCode\":null,\n" +
                "            \"productName\":null,\n" +
                "            \"approverCode\":null,\n" +
                "            \"pureRate\":1,\n" +
                "            \"discount\":null,\n" +
                "            \"insuredCount\":null,\n" +
                "            \"auditNo\":null,\n" +
                "            \"auditNoToE\":null,\n" +
                "            \"crossSellType\":\"3\",\n" +
                "            \"inputSumPremium\":null,\n" +
                "            \"dutySumNetPremium\":null,\n" +
                "            \"freeSumNetPremium\":null,\n" +
                "            \"orderNo\":null,\n" +
                "            \"orderFlag\":null,\n" +
                "            \"policyPageVo\":null,\n" +
                "            \"prpCmainAccs\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpCmainExts\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpCmainBonds\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpCmainCredits\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpCextendInfos\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpCmainAirLines\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpCcommissions\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpCcoeffs\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpCmainAgris\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpCprojects\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpCprofitFactors\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpCitemCreditOths\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpCclauseplans\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpCpayeeAccounts\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"prpCinsuredCreditInvests\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"actualProduct\":\"\",\n" +
                "            \"prpCmainExtraVo\":null,\n" +
                "            \"Employees\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"Props\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"AgentDetails\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"shipDrivers\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"Drivers\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"Addresses\":[\n" +
                "                {\n" +
                "                    \"id\":{\n" +
                "                        \"pkey\":\"PZIP202033016000000038\",\n" +
                "                        \"addressNo\":1\n" +
                "                    },\n" +
                "                    \"addressNo\":null,\n" +
                "                    \"proposalNo\":\"TZIP202033016000000090\",\n" +
                "                    \"policyNo\":\"PZIP202033016000000038\",\n" +
                "                    \"riskCode\":\"ZIP\",\n" +
                "                    \"postCode\":\"100098\",\n" +
                "                    \"addressCode\":null,\n" +
                "                    \"addressName\":\"2147483647\",\n" +
                "                    \"accountDate\":null,\n" +
                "                    \"businessSource\":null,\n" +
                "                    \"businessClass\":null,\n" +
                "                    \"preFingure\":null,\n" +
                "                    \"preChar\":null,\n" +
                "                    \"flag\":null,\n" +
                "                    \"insertTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"operateTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"tkey\":\"2020-11-25 22:12:55\",\n" +
                "                    \"latitude\":null,\n" +
                "                    \"longitude\":null,\n" +
                "                    \"provCode\":null,\n" +
                "                    \"cityCode\":null,\n" +
                "                    \"countyCode\":null,\n" +
                "                    \"importFlag\":null\n" +
                "                }\n" +
                "            ],\n" +
                "            \"InsuredIdvLists\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"Crosses\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"Subs\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"Agents\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"Constructs\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"Itemkinds\":[\n" +
                "                {\n" +
                "                    \"id\":{\n" +
                "                        \"pkey\":\"PZIP202033016000000038\",\n" +
                "                        \"itemKindNo\":1\n" +
                "                    },\n" +
                "                    \"riskCode\":\"ZIP\",\n" +
                "                    \"familyNo\":null,\n" +
                "                    \"familyName\":null,\n" +
                "                    \"projectCode\":\"\",\n" +
                "                    \"clauseCode\":\"100996\",\n" +
                "                    \"clauseName\":\"国内水路客运承运人责任保险条款\",\n" +
                "                    \"kindCode\":\"101172\",\n" +
                "                    \"kindName\":\"国内水路客运承运人责任\",\n" +
                "                    \"itemNo\":1,\n" +
                "                    \"itemCode\":null,\n" +
                "                    \"itemDetailName\":null,\n" +
                "                    \"groupNo\":1,\n" +
                "                    \"modeCode\":null,\n" +
                "                    \"modeName\":null,\n" +
                "                    \"startDate\":\"2020-11-26\",\n" +
                "                    \"startHour\":19,\n" +
                "                    \"endDate\":\"2021-11-25\",\n" +
                "                    \"endHour\":19,\n" +
                "                    \"model\":null,\n" +
                "                    \"buyDate\":null,\n" +
                "                    \"addressNo\":1,\n" +
                "                    \"calculateFlag\":\"1\",\n" +
                "                    \"currency\":\"CNY\",\n" +
                "                    \"unitAmount\":3270,\n" +
                "                    \"quantity\":1,\n" +
                "                    \"unit\":\"1\",\n" +
                "                    \"value\":null,\n" +
                "                    \"amount\":3270,\n" +
                "                    \"ratePeriod\":null,\n" +
                "                    \"rate\":1,\n" +
                "                    \"shortRateFlag\":\"3\",\n" +
                "                    \"shortRate\":100,\n" +
                "                    \"prePremium\":null,\n" +
                "                    \"calPremium\":1,\n" +
                "                    \"basePremium\":null,\n" +
                "                    \"benchMarkPremium\":null,\n" +
                "                    \"discount\":1,\n" +
                "                    \"adjustRate\":1,\n" +
                "                    \"unitPremium\":null,\n" +
                "                    \"premiumB4Discount\":null,\n" +
                "                    \"premium\":1,\n" +
                "                    \"deductibleRate\":null,\n" +
                "                    \"deductible\":null,\n" +
                "                    \"taxFee\":0.06,\n" +
                "                    \"taxFee_ys\":null,\n" +
                "                    \"taxFee_gb\":0,\n" +
                "                    \"taxFee_lb\":0,\n" +
                "                    \"netPremium\":0.94,\n" +
                "                    \"allTaxFee\":0.06,\n" +
                "                    \"allNetPremium\":0.94,\n" +
                "                    \"taxRate\":6,\n" +
                "                    \"taxFlag\":\"2\",\n" +
                "                    \"flag\":null,\n" +
                "                    \"insertTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"operateTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"policyNo\":\"PZIP202033016000000038\",\n" +
                "                    \"proposalNo\":\"TZIP202033016000000090\",\n" +
                "                    \"tkey\":\"2020-11-25 22:12:55\",\n" +
                "                    \"prpCprofits\":[\n" +
                "\n" +
                "                    ],\n" +
                "                    \"iscalculateFlag\":null,\n" +
                "                    \"userCount\":null,\n" +
                "                    \"pack\":null,\n" +
                "                    \"firstLevel\":null,\n" +
                "                    \"methodType\":\"1\",\n" +
                "                    \"insuredQuantity\":1,\n" +
                "                    \"clauseFlag\":\"1\",\n" +
                "                    \"itemKindFactorFlag\":null,\n" +
                "                    \"prpCitemKindTaxFees\":[\n" +
                "\n" +
                "                    ],\n" +
                "                    \"ItemKindDetails\":[\n" +
                "\n" +
                "                    ]\n" +
                "                }\n" +
                "            ],\n" +
                "            \"Limits\":[\n" +
                "                {\n" +
                "                    \"id\":{\n" +
                "                        \"pkey\":\"PZIP202033016000000038\",\n" +
                "                        \"limitGrade\":\"2\",\n" +
                "                        \"limitNo\":1,\n" +
                "                        \"limitType\":\"000049\",\n" +
                "                        \"currency\":\"CNY\"\n" +
                "                    },\n" +
                "                    \"riskCode\":\"ZIP\",\n" +
                "                    \"limitFee\":1000,\n" +
                "                    \"calculateFlag\":\"0\",\n" +
                "                    \"limitFlag\":\"0\",\n" +
                "                    \"isRecorded\":\"0\",\n" +
                "                    \"flag\":\"0\",\n" +
                "                    \"insertTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"operateTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"proposalNo\":\"TZIP202033016000000090\",\n" +
                "                    \"policyNo\":\"PZIP202033016000000038\",\n" +
                "                    \"tkey\":\"2020-11-25 22:12:55\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\":{\n" +
                "                        \"pkey\":\"PZIP202033016000000038\",\n" +
                "                        \"limitGrade\":\"2\",\n" +
                "                        \"limitNo\":1,\n" +
                "                        \"limitType\":\"000090\",\n" +
                "                        \"currency\":\"CNY\"\n" +
                "                    },\n" +
                "                    \"riskCode\":\"ZIP\",\n" +
                "                    \"limitFee\":1000,\n" +
                "                    \"calculateFlag\":\"0\",\n" +
                "                    \"limitFlag\":\"1\",\n" +
                "                    \"isRecorded\":\"0\",\n" +
                "                    \"flag\":\"0\",\n" +
                "                    \"insertTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"operateTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"proposalNo\":\"TZIP202033016000000090\",\n" +
                "                    \"policyNo\":\"PZIP202033016000000038\",\n" +
                "                    \"tkey\":\"2020-11-25 22:12:55\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\":{\n" +
                "                        \"pkey\":\"PZIP202033016000000038\",\n" +
                "                        \"limitGrade\":\"2\",\n" +
                "                        \"limitNo\":1,\n" +
                "                        \"limitType\":\"000091\",\n" +
                "                        \"currency\":\"CNY\"\n" +
                "                    },\n" +
                "                    \"riskCode\":\"ZIP\",\n" +
                "                    \"limitFee\":1000,\n" +
                "                    \"calculateFlag\":\"0\",\n" +
                "                    \"limitFlag\":\"0\",\n" +
                "                    \"isRecorded\":\"0\",\n" +
                "                    \"flag\":\"0\",\n" +
                "                    \"insertTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"operateTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"proposalNo\":\"TZIP202033016000000090\",\n" +
                "                    \"policyNo\":\"PZIP202033016000000038\",\n" +
                "                    \"tkey\":\"2020-11-25 22:12:55\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\":{\n" +
                "                        \"pkey\":\"PZIP202033016000000038\",\n" +
                "                        \"limitGrade\":\"2\",\n" +
                "                        \"limitNo\":1,\n" +
                "                        \"limitType\":\"000098\",\n" +
                "                        \"currency\":\"CNY\"\n" +
                "                    },\n" +
                "                    \"riskCode\":\"ZIP\",\n" +
                "                    \"limitFee\":1000,\n" +
                "                    \"calculateFlag\":\"0\",\n" +
                "                    \"limitFlag\":\"0\",\n" +
                "                    \"isRecorded\":\"0\",\n" +
                "                    \"flag\":\"0\",\n" +
                "                    \"insertTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"operateTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"proposalNo\":\"TZIP202033016000000090\",\n" +
                "                    \"policyNo\":\"PZIP202033016000000038\",\n" +
                "                    \"tkey\":\"2020-11-25 22:12:55\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\":{\n" +
                "                        \"pkey\":\"PZIP202033016000000038\",\n" +
                "                        \"limitGrade\":\"2\",\n" +
                "                        \"limitNo\":1,\n" +
                "                        \"limitType\":\"000099\",\n" +
                "                        \"currency\":\"CNY\"\n" +
                "                    },\n" +
                "                    \"riskCode\":\"ZIP\",\n" +
                "                    \"limitFee\":1000,\n" +
                "                    \"calculateFlag\":\"0\",\n" +
                "                    \"limitFlag\":\"0\",\n" +
                "                    \"isRecorded\":\"0\",\n" +
                "                    \"flag\":\"0\",\n" +
                "                    \"insertTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"operateTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"proposalNo\":\"TZIP202033016000000090\",\n" +
                "                    \"policyNo\":\"PZIP202033016000000038\",\n" +
                "                    \"tkey\":\"2020-11-25 22:12:55\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\":{\n" +
                "                        \"pkey\":\"PZIP202033016000000038\",\n" +
                "                        \"limitGrade\":\"2\",\n" +
                "                        \"limitNo\":1,\n" +
                "                        \"limitType\":\"000113\",\n" +
                "                        \"currency\":\"CNY\"\n" +
                "                    },\n" +
                "                    \"riskCode\":\"ZIP\",\n" +
                "                    \"limitFee\":1000,\n" +
                "                    \"calculateFlag\":\"0\",\n" +
                "                    \"limitFlag\":\"0\",\n" +
                "                    \"isRecorded\":\"0\",\n" +
                "                    \"flag\":\"0\",\n" +
                "                    \"insertTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"operateTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"proposalNo\":\"TZIP202033016000000090\",\n" +
                "                    \"policyNo\":\"PZIP202033016000000038\",\n" +
                "                    \"tkey\":\"2020-11-25 22:12:55\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\":{\n" +
                "                        \"pkey\":\"PZIP202033016000000038\",\n" +
                "                        \"limitGrade\":\"2\",\n" +
                "                        \"limitNo\":1,\n" +
                "                        \"limitType\":\"000137\",\n" +
                "                        \"currency\":\"CNY\"\n" +
                "                    },\n" +
                "                    \"riskCode\":\"ZIP\",\n" +
                "                    \"limitFee\":1000,\n" +
                "                    \"calculateFlag\":\"0\",\n" +
                "                    \"limitFlag\":\"1\",\n" +
                "                    \"isRecorded\":\"0\",\n" +
                "                    \"flag\":\"0\",\n" +
                "                    \"insertTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"operateTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"proposalNo\":\"TZIP202033016000000090\",\n" +
                "                    \"policyNo\":\"PZIP202033016000000038\",\n" +
                "                    \"tkey\":\"2020-11-25 22:12:55\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"Loans\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"Engages\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"Insureds\":[\n" +
                "                {\n" +
                "                    \"id\":{\n" +
                "                        \"pkey\":\"PZIP202033016000000038\",\n" +
                "                        \"serialNo\":1\n" +
                "                    },\n" +
                "                    \"serialNo\":null,\n" +
                "                    \"proposalNo\":\"TZIP202033016000000090\",\n" +
                "                    \"policyNo\":\"PZIP202033016000000038\",\n" +
                "                    \"tkey\":\"2020-11-25 22:12:55\",\n" +
                "                    \"riskCode\":\"ZIP\",\n" +
                "                    \"language\":\"C\",\n" +
                "                    \"insuredType\":\"1\",\n" +
                "                    \"insuredCode\":\"3200100000038747\",\n" +
                "                    \"insuredName\":\"芈月\",\n" +
                "                    \"insuredEName\":null,\n" +
                "                    \"aliasName\":null,\n" +
                "                    \"insuredAddress\":\"北京市市辖区霍营乡资金新干线4区9号楼2单元1340\",\n" +
                "                    \"insuredNature\":\"3\",\n" +
                "                    \"insuredFlag\":\"110000000000000000000000000000\",\n" +
                "                    \"unitType\":null,\n" +
                "                    \"appendPrintName\":null,\n" +
                "                    \"insuredIdentity\":null,\n" +
                "                    \"relateSerialNo\":null,\n" +
                "                    \"identifyType\":\"01\",\n" +
                "                    \"identifyNumber\":\"320301197105171869\",\n" +
                "                    \"unifiedSocialCreditCode\":null,\n" +
                "                    \"creditLevel\":null,\n" +
                "                    \"possessNature\":null,\n" +
                "                    \"businessSource\":null,\n" +
                "                    \"businessSort\":null,\n" +
                "                    \"occupationCode\":null,\n" +
                "                    \"educationCode\":null,\n" +
                "                    \"bank\":null,\n" +
                "                    \"accountName\":null,\n" +
                "                    \"account\":null,\n" +
                "                    \"linkerName\":null,\n" +
                "                    \"postAddress\":null,\n" +
                "                    \"postCode\":\"100098\",\n" +
                "                    \"phoneNumber\":null,\n" +
                "                    \"faxNumber\":null,\n" +
                "                    \"mobile\":\"15006923049\",\n" +
                "                    \"netAddress\":null,\n" +
                "                    \"email\":\"123123239@qq.com\",\n" +
                "                    \"dateValid\":null,\n" +
                "                    \"startDate\":\"2020-11-26 00:00:00\",\n" +
                "                    \"endDate\":\"2021-11-25 00:00:00\",\n" +
                "                    \"benefitFlag\":null,\n" +
                "                    \"benefitRate\":null,\n" +
                "                    \"drivingLicenseNo\":null,\n" +
                "                    \"changelessFlag\":null,\n" +
                "                    \"sex\":null,\n" +
                "                    \"age\":49,\n" +
                "                    \"marriage\":null,\n" +
                "                    \"driverAddress\":null,\n" +
                "                    \"peccancy\":null,\n" +
                "                    \"acceptLicenseDate\":null,\n" +
                "                    \"receiveLicenseYear\":null,\n" +
                "                    \"drivingYears\":null,\n" +
                "                    \"causeTroubleTimes\":null,\n" +
                "                    \"awardLicenseOrgan\":null,\n" +
                "                    \"drivingCarType\":null,\n" +
                "                    \"countryCode\":\"CHN\",\n" +
                "                    \"versionNo\":null,\n" +
                "                    \"auditstatus\":null,\n" +
                "                    \"flag\":null,\n" +
                "                    \"warningFlag\":null,\n" +
                "                    \"insertTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"operateTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"blackFlag\":null,\n" +
                "                    \"importSerialNo\":null,\n" +
                "                    \"groupCode\":null,\n" +
                "                    \"groupName\":null,\n" +
                "                    \"dweller\":\"A\",\n" +
                "                    \"customerLevel\":null,\n" +
                "                    \"insuredPYName\":null,\n" +
                "                    \"groupNo\":1,\n" +
                "                    \"itemNo\":null,\n" +
                "                    \"importFlag\":null,\n" +
                "                    \"smsFlag\":null,\n" +
                "                    \"emailFlag\":null,\n" +
                "                    \"sendPhone\":null,\n" +
                "                    \"sendEmail\":null,\n" +
                "                    \"subPolicyNo\":null,\n" +
                "                    \"socialSecurityNo\":null,\n" +
                "                    \"electronicflag\":\"1\",\n" +
                "                    \"insuredSort\":null,\n" +
                "                    \"isHealthSurvey\":null,\n" +
                "                    \"InsuredNatures\":[\n" +
                "                        {\n" +
                "                            \"id\":{\n" +
                "                                \"pkey\":\"PZIP202033016000000038\",\n" +
                "                                \"serialNo\":1\n" +
                "                            },\n" +
                "                            \"serialNo\":null,\n" +
                "                            \"proposalNo\":\"TZIP202033016000000090\",\n" +
                "                            \"policyNo\":\"PZIP202033016000000038\",\n" +
                "                            \"tkey\":\"2020-11-25 22:12:55\",\n" +
                "                            \"insuredFlag\":\"110000000000000000000000000000\",\n" +
                "                            \"sex\":\"2\",\n" +
                "                            \"age\":49,\n" +
                "                            \"birthday\":\"1971-05-17\",\n" +
                "                            \"health\":null,\n" +
                "                            \"jobTitle\":null,\n" +
                "                            \"localWorkYears\":null,\n" +
                "                            \"education\":null,\n" +
                "                            \"totalWorkYears\":null,\n" +
                "                            \"unit\":null,\n" +
                "                            \"unitPhoneNumber\":null,\n" +
                "                            \"unitAddress\":null,\n" +
                "                            \"unitPostCode\":null,\n" +
                "                            \"unitType\":null,\n" +
                "                            \"dutyLevel\":null,\n" +
                "                            \"dutyType\":null,\n" +
                "                            \"occupationCode\":null,\n" +
                "                            \"houseProperty\":null,\n" +
                "                            \"localPoliceStation\":null,\n" +
                "                            \"roomAddress\":null,\n" +
                "                            \"roomPostCode\":null,\n" +
                "                            \"selfMonthIncome\":null,\n" +
                "                            \"familyMonthIncome\":null,\n" +
                "                            \"incomeSource\":null,\n" +
                "                            \"roomPhone\":null,\n" +
                "                            \"mobile\":null,\n" +
                "                            \"familySumQuantity\":null,\n" +
                "                            \"marriage\":null,\n" +
                "                            \"spouseName\":null,\n" +
                "                            \"spouseBornDate\":null,\n" +
                "                            \"spouseId\":null,\n" +
                "                            \"spouseMobile\":null,\n" +
                "                            \"spouseUnit\":null,\n" +
                "                            \"spouseJobTitle\":null,\n" +
                "                            \"spouseUnitPhone\":\"1\",\n" +
                "                            \"flag\":null,\n" +
                "                            \"carType\":null,\n" +
                "                            \"disablePartAndLevel\":null,\n" +
                "                            \"moreLoanHouseFlag\":null,\n" +
                "                            \"nation\":null,\n" +
                "                            \"poorFlag\":null,\n" +
                "                            \"licenseNo\":null,\n" +
                "                            \"getLicenseDate\":null,\n" +
                "                            \"insertTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                            \"operateTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                            \"educationCode\":null,\n" +
                "                            \"contactNo\":null,\n" +
                "                            \"contactName\":null,\n" +
                "                            \"certificationDate\":null,\n" +
                "                            \"certificationNo\":null,\n" +
                "                            \"addressCount\":null,\n" +
                "                            \"importFlag\":null,\n" +
                "                            \"socialFlag\":null,\n" +
                "                            \"cardAmount\":null,\n" +
                "                            \"usedAmount\":null,\n" +
                "                            \"payAmount\":null,\n" +
                "                            \"isPoverty\":null,\n" +
                "                            \"importSerialNo\":null\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"InsuredArtifs\":[\n" +
                "                        {\n" +
                "                            \"id\":{\n" +
                "                                \"pkey\":\"PZIP202033016000000038\",\n" +
                "                                \"serialNo\":1\n" +
                "                            },\n" +
                "                            \"serialNo\":null,\n" +
                "                            \"proposalNo\":\"TZIP202033016000000090\",\n" +
                "                            \"policyNo\":\"PZIP202033016000000038\",\n" +
                "                            \"tkey\":\"2020-11-25 22:12:55\",\n" +
                "                            \"insuredFlag\":\"110000000000000000000000000000\",\n" +
                "                            \"leaderName\":null,\n" +
                "                            \"leaderId\":null,\n" +
                "                            \"leaderMobile\":null,\n" +
                "                            \"leaderUnitPhone\":null,\n" +
                "                            \"unitAddress\":null,\n" +
                "                            \"phoneNumber\":null,\n" +
                "                            \"postCode\":null,\n" +
                "                            \"businessCode\":null,\n" +
                "                            \"revenueRegistNo\":null,\n" +
                "                            \"carType\":null,\n" +
                "                            \"flag\":null,\n" +
                "                            \"insertTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                            \"operateTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                            \"possessNature\":null,\n" +
                "                            \"businessSource\":null,\n" +
                "                            \"businessSort\":null,\n" +
                "                            \"enterpriseNature\":null,\n" +
                "                            \"qualification\":null,\n" +
                "                            \"project\":null,\n" +
                "                            \"deposit\":null,\n" +
                "                            \"assLiabRate\":null,\n" +
                "                            \"enterpriseScale\":null,\n" +
                "                            \"operationTime\":null,\n" +
                "                            \"lastYinCome\":null,\n" +
                "                            \"registerdCapita\":null,\n" +
                "                            \"proStartDate\":null,\n" +
                "                            \"proEndDate\":null,\n" +
                "                            \"importFlag\":null,\n" +
                "                            \"importSerialNo\":null\n" +
                "                        }\n" +
                "                    ]\n" +
                "                }\n" +
                "            ],\n" +
                "            \"Coins\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"SpecialFacs\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"Batches\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"Commissions\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"Cargos\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"Plans\":[\n" +
                "                {\n" +
                "                    \"tkey\":\"2020-11-25 22:12:55\",\n" +
                "                    \"proposalNo\":\"TZIP202033016000000090\",\n" +
                "                    \"policyNo\":\"PZIP202033016000000038\",\n" +
                "                    \"id\":{\n" +
                "                        \"pkey\":\"PZIP202033016000000038\",\n" +
                "                        \"serialNo\":1\n" +
                "                    },\n" +
                "                    \"endorseNo\":null,\n" +
                "                    \"payNo\":1,\n" +
                "                    \"payReason\":\"R21\",\n" +
                "                    \"planDate\":\"2020-11-26\",\n" +
                "                    \"currency\":\"CNY\",\n" +
                "                    \"subsidyrate\":null,\n" +
                "                    \"planFee\":1,\n" +
                "                    \"delinquentFee\":1,\n" +
                "                    \"flag\":null,\n" +
                "                    \"payDate\":\"2020-11-25 00:00:00\",\n" +
                "                    \"insertTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"operateTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"payType\":null,\n" +
                "                    \"exchangeNo\":null,\n" +
                "                    \"paymentcomplete\":null,\n" +
                "                    \"taxFee\":0.06\n" +
                "                }\n" +
                "            ],\n" +
                "            \"CoinsDetails\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"Liabs\":[\n" +
                "                {\n" +
                "                    \"proposalNo\":\"TZIP202033016000000090\",\n" +
                "                    \"policyNo\":\"PZIP202033016000000038\",\n" +
                "                    \"riskCode\":\"ZIP\",\n" +
                "                    \"certificateNo\":null,\n" +
                "                    \"certificateDate\":null,\n" +
                "                    \"certificateDepart\":null,\n" +
                "                    \"practiceDate\":null,\n" +
                "                    \"businessDetail\":\"1\",\n" +
                "                    \"businessSite\":\"营业处所1579507770790\",\n" +
                "                    \"insureAreaCode\":\"01\",\n" +
                "                    \"insureArea\":null,\n" +
                "                    \"saleArea\":null,\n" +
                "                    \"officeType\":\"27\",\n" +
                "                    \"bkWardStartDate\":null,\n" +
                "                    \"bkWardEndDate\":null,\n" +
                "                    \"staffCount\":null,\n" +
                "                    \"preTurnOver\":null,\n" +
                "                    \"nowTurnOver\":null,\n" +
                "                    \"electricPower\":null,\n" +
                "                    \"remark\":null,\n" +
                "                    \"claimBase\":\"2\",\n" +
                "                    \"flag\":null,\n" +
                "                    \"guaranteeArea\":null,\n" +
                "                    \"familyMembers\":null,\n" +
                "                    \"goodsName\":null,\n" +
                "                    \"hazardLevel\":null,\n" +
                "                    \"totleCount\":null,\n" +
                "                    \"quantity\":null,\n" +
                "                    \"itemInfo\":null,\n" +
                "                    \"disputeType\":null,\n" +
                "                    \"court\":null,\n" +
                "                    \"lineType\":null,\n" +
                "                    \"insuredType\":null,\n" +
                "                    \"isSignInsured\":null,\n" +
                "                    \"collectBsae\":null,\n" +
                "                    \"businessClass\":null,\n" +
                "                    \"companyLevel\":null,\n" +
                "                    \"companyType\":null,\n" +
                "                    \"insertTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"operateTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"pkey\":\"PZIP202033016000000038\",\n" +
                "                    \"tkey\":\"2020-11-25 22:12:55\",\n" +
                "                    \"singlePayRate\":null,\n" +
                "                    \"ensureCardNo\":null\n" +
                "                }\n" +
                "            ],\n" +
                "            \"Confines\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"Rations\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"Items\":[\n" +
                "                {\n" +
                "                    \"id\":{\n" +
                "                        \"pkey\":\"PZIP202033016000000038\",\n" +
                "                        \"itemNo\":1\n" +
                "                    },\n" +
                "                    \"proposalNo\":\"TZIP202033016000000090\",\n" +
                "                    \"policyNo\":\"PZIP202033016000000038\",\n" +
                "                    \"riskCode\":\"ZIP\",\n" +
                "                    \"itemCode\":null,\n" +
                "                    \"itemName\":null,\n" +
                "                    \"plusRate\":null,\n" +
                "                    \"addressNo\":null,\n" +
                "                    \"flag\":null,\n" +
                "                    \"itemInfo\":null,\n" +
                "                    \"insertTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"operateTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"tkey\":\"2020-11-25 22:12:55\",\n" +
                "                    \"prpCitemOils\":[\n" +
                "\n" +
                "                    ],\n" +
                "                    \"prpCitemDevices\":[\n" +
                "\n" +
                "                    ],\n" +
                "                    \"prpCitemTravelAgencys\":[\n" +
                "\n" +
                "                    ],\n" +
                "                    \"Vehicles\":[\n" +
                "\n" +
                "                    ],\n" +
                "                    \"ItemCargos\":[\n" +
                "\n" +
                "                    ],\n" +
                "                    \"ItemShips\":[\n" +
                "                        {\n" +
                "                            \"id\":{\n" +
                "                                \"pkey\":\"PZIP202033016000000038\",\n" +
                "                                \"itemNo\":1\n" +
                "                            },\n" +
                "                            \"proposalNo\":\"TZIP202033016000000090\",\n" +
                "                            \"itemNo\":null,\n" +
                "                            \"policyNo\":\"PZIP202033016000000038\",\n" +
                "                            \"riskCode\":\"ZIP\",\n" +
                "                            \"fleetNo\":null,\n" +
                "                            \"ditemno\":null,\n" +
                "                            \"shipCode\":null,\n" +
                "                            \"shipCName\":\"2147483647\",\n" +
                "                            \"shipEName\":null,\n" +
                "                            \"oldShipName\":null,\n" +
                "                            \"shipOwner\":null,\n" +
                "                            \"oldShipOwner\":null,\n" +
                "                            \"conveyManager\":null,\n" +
                "                            \"associate\":null,\n" +
                "                            \"makeYearMonth\":null,\n" +
                "                            \"countryCode\":null,\n" +
                "                            \"makeFactory\":null,\n" +
                "                            \"makeDock\":null,\n" +
                "                            \"shipWayType\":null,\n" +
                "                            \"applyCriterion\":null,\n" +
                "                            \"makeContractNo\":null,\n" +
                "                            \"makeStartDate\":null,\n" +
                "                            \"makeEndDate\":null,\n" +
                "                            \"preBuildCyc\":null,\n" +
                "                            \"stepHull\":null,\n" +
                "                            \"oldStepHull\":null,\n" +
                "                            \"shipFlag\":null,\n" +
                "                            \"shipTypeCode\":null,\n" +
                "                            \"useNatureCode\":null,\n" +
                "                            \"shipUsage\":null,\n" +
                "                            \"shipStruct\":null,\n" +
                "                            \"registrySite\":null,\n" +
                "                            \"tonCount\":null,\n" +
                "                            \"netTonCount\":null,\n" +
                "                            \"horsePower\":null,\n" +
                "                            \"powerUnit\":null,\n" +
                "                            \"seatCount\":500,\n" +
                "                            \"loadTon\":null,\n" +
                "                            \"shipLength\":null,\n" +
                "                            \"shipWidth\":null,\n" +
                "                            \"shipDepth\":null,\n" +
                "                            \"trySailPeriod\":null,\n" +
                "                            \"trySailArea\":null,\n" +
                "                            \"shipPort\":null,\n" +
                "                            \"launchDate\":null,\n" +
                "                            \"sailAreaCode\":null,\n" +
                "                            \"sailAreaName\":null,\n" +
                "                            \"sailScope\":null,\n" +
                "                            \"sailModeCode\":null,\n" +
                "                            \"voyage\":null,\n" +
                "                            \"shipValue\":null,\n" +
                "                            \"currency\":null,\n" +
                "                            \"suspendStartDate\":null,\n" +
                "                            \"suspendEndDate\":null,\n" +
                "                            \"mortgageName\":null,\n" +
                "                            \"insurerShipRelation\":null,\n" +
                "                            \"shipCallSign\":null,\n" +
                "                            \"imo\":null,\n" +
                "                            \"shipManagerAddress\":null,\n" +
                "                            \"reconstructionYear\":null,\n" +
                "                            \"shipAssociation\":null,\n" +
                "                            \"fleetTotalScale\":null,\n" +
                "                            \"fleetPICCScale\":null,\n" +
                "                            \"shipConstractionAdrdress\":null,\n" +
                "                            \"launchType\":null,\n" +
                "                            \"govAgencyForShip\":null,\n" +
                "                            \"licStartDateForShip\":null,\n" +
                "                            \"licEndDateForShip\":null,\n" +
                "                            \"govAgencyForHull\":null,\n" +
                "                            \"licStartDateForHull\":null,\n" +
                "                            \"licEndDateForHull\":null,\n" +
                "                            \"govAgencyForTempHull\":null,\n" +
                "                            \"licStartDateForTempHull\":null,\n" +
                "                            \"licenceEndDateForTempHull\":null,\n" +
                "                            \"govAgencyForMachinery\":null,\n" +
                "                            \"licStartDateForMachinery\":null,\n" +
                "                            \"licEndDateForMachinery\":null,\n" +
                "                            \"govAgencyForTempMachinery\":null,\n" +
                "                            \"licStartDateForTempMachinery\":null,\n" +
                "                            \"licEndDateForTempMachinery\":null,\n" +
                "                            \"isLicForIntTon\":null,\n" +
                "                            \"licStartDateForIntTon\":null,\n" +
                "                            \"licEndDateForIntTon\":null,\n" +
                "                            \"isLicForIntLoad\":null,\n" +
                "                            \"licStartDateForIntLoad\":null,\n" +
                "                            \"licEndDateForIntLoad\":null,\n" +
                "                            \"isLicForConstruction\":null,\n" +
                "                            \"licStartDateForConstruction\":null,\n" +
                "                            \"licEndDateForConstruction\":null,\n" +
                "                            \"isLicForEquipment\":null,\n" +
                "                            \"licStartDateForEquipment\":null,\n" +
                "                            \"licEndDateForEquipment\":null,\n" +
                "                            \"isLicForWireLess\":null,\n" +
                "                            \"licStartDateForWireLess\":null,\n" +
                "                            \"licEndDateForWireLess\":null,\n" +
                "                            \"isLicForLift\":null,\n" +
                "                            \"licStartDateForLift\":null,\n" +
                "                            \"licEndDateForLift\":null,\n" +
                "                            \"isLicForFitness\":null,\n" +
                "                            \"licStartDateForFitness\":null,\n" +
                "                            \"licEndDateForFitness\":null,\n" +
                "                            \"isLicForIOPP\":null,\n" +
                "                            \"licStartDateForIOPP\":null,\n" +
                "                            \"licEndDateForIOPP\":null,\n" +
                "                            \"isLicForNavigation\":null,\n" +
                "                            \"licStartDateForNavigation\":null,\n" +
                "                            \"licEndDateForNavigation\":null,\n" +
                "                            \"isLicForSMC\":null,\n" +
                "                            \"licStartDateForSMC\":null,\n" +
                "                            \"licEndDateForSMC\":null,\n" +
                "                            \"isLicForConform\":null,\n" +
                "                            \"licStartDateForConform\":null,\n" +
                "                            \"licEndDateForConform\":null,\n" +
                "                            \"isLicForMSM\":null,\n" +
                "                            \"licStartDateForMSM\":null,\n" +
                "                            \"licEndDateForMSM\":null,\n" +
                "                            \"makeDockAddress\":null,\n" +
                "                            \"projectForBuilder\":null,\n" +
                "                            \"constractNoForBuilder\":null,\n" +
                "                            \"amountForBuilder\":null,\n" +
                "                            \"addressForBuilder\":null,\n" +
                "                            \"projectForSub\":null,\n" +
                "                            \"constractNoForSub\":null,\n" +
                "                            \"addressForSub\":null,\n" +
                "                            \"historyForBuilder\":null,\n" +
                "                            \"lastPrice\":null,\n" +
                "                            \"deliveryPlace\":null,\n" +
                "                            \"beginDate\":null,\n" +
                "                            \"stageDate\":null,\n" +
                "                            \"waterDate\":null,\n" +
                "                            \"sailDate\":null,\n" +
                "                            \"deliveryDate\":null,\n" +
                "                            \"insuredStatus\":null,\n" +
                "                            \"actualShipOwner\":null,\n" +
                "                            \"loadType\":null,\n" +
                "                            \"loadStyle\":null,\n" +
                "                            \"shipRegisterOwner\":null,\n" +
                "                            \"shipRegisterPlace\":null,\n" +
                "                            \"newContractPrice\":null,\n" +
                "                            \"remark\":null,\n" +
                "                            \"flag\":null,\n" +
                "                            \"insertTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                            \"operateTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                            \"tkey\":\"2020-11-25 22:12:55\",\n" +
                "                            \"voyageNo\":null,\n" +
                "                            \"registryPort\":null,\n" +
                "                            \"shipYears\":null,\n" +
                "                            \"landAgent\":null,\n" +
                "                            \"shipLeval\":null,\n" +
                "                            \"route\":null,\n" +
                "                            \"aquageGrade\":\"2147483647\",\n" +
                "                            \"importFlag\":null,\n" +
                "                            \"engineNo\":null,\n" +
                "                            \"vinNo\":null,\n" +
                "                            \"departurePlace\":null,\n" +
                "                            \"destination\":null,\n" +
                "                            \"insured\":null,\n" +
                "                            \"CertificateNo\":\"110115\"\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"ItemConstructs\":[\n" +
                "\n" +
                "                    ],\n" +
                "                    \"ItemPlanes\":[\n" +
                "\n" +
                "                    ],\n" +
                "                    \"ItemProps\":[\n" +
                "\n" +
                "                    ],\n" +
                "                    \"ItemHouses\":[\n" +
                "\n" +
                "                    ]\n" +
                "                }\n" +
                "            ],\n" +
                "            \"Fees\":[\n" +
                "                {\n" +
                "                    \"id\":{\n" +
                "                        \"pkey\":\"PZIP202033016000000038\",\n" +
                "                        \"currency\":\"CNY\"\n" +
                "                    },\n" +
                "                    \"proposalNo\":\"TZIP202033016000000090\",\n" +
                "                    \"policyNo\":\"PZIP202033016000000038\",\n" +
                "                    \"tkey\":\"2020-11-25 22:12:55\",\n" +
                "                    \"riskCode\":\"ZIP\",\n" +
                "                    \"amount\":3270,\n" +
                "                    \"premiumB4Discount\":null,\n" +
                "                    \"premium\":1,\n" +
                "                    \"flag\":\"\",\n" +
                "                    \"insertTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"operateTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"sumTaxFee\":0.06,\n" +
                "                    \"sumTaxFee_ys\":0,\n" +
                "                    \"sumNetPremium\":0.94,\n" +
                "                    \"sumTaxFee_gb\":0,\n" +
                "                    \"sumTaxFee_lb\":0\n" +
                "                }\n" +
                "            ],\n" +
                "            \"Renewals\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"CargoDetails\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"Cprotocols\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"Clauses\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"Contributions\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"Commons\":[\n" +
                "                {\n" +
                "                    \"pkey\":\"PZIP202033016000000038\",\n" +
                "                    \"tkey\":\"2020-11-25 22:12:55\",\n" +
                "                    \"proposalNo\":\"TZIP202033016000000090\",\n" +
                "                    \"specialFlag\":\"   0  0        \",\n" +
                "                    \"ext1\":null,\n" +
                "                    \"ext2\":null,\n" +
                "                    \"ext3\":null,\n" +
                "                    \"resourceCode\":null,\n" +
                "                    \"resourceName\":null,\n" +
                "                    \"qualityLevel\":null,\n" +
                "                    \"insertTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"operateTimeForHis\":\"2020-11-25 22:12:56\",\n" +
                "                    \"newBusinessNature\":\"020\",\n" +
                "                    \"scmsAuditNotion\":null,\n" +
                "                    \"pay_method\":null,\n" +
                "                    \"platformProjectCode\":null,\n" +
                "                    \"handler1Code_uni\":\"1233134176\",\n" +
                "                    \"handlerCode_uni\":\"1233134176\",\n" +
                "                    \"commonFlag\":\"0   0     0\",\n" +
                "                    \"otherPolicyName\":\"2147483647\",\n" +
                "                    \"groupName\":null,\n" +
                "                    \"isHPDriveCus\":\"0\",\n" +
                "                    \"startTime\":\"18:51\",\n" +
                "                    \"endTime\":\"18:51\",\n" +
                "                    \"salesCode\":\"83298873\",\n" +
                "                    \"electronic\":\"1\",\n" +
                "                    \"electronicTitle\":\"0\",\n" +
                "                    \"electronicPhone\":null,\n" +
                "                    \"socialinsPay\":null,\n" +
                "                    \"socialinsNo\":null,\n" +
                "                    \"projectCode\":null,\n" +
                "                    \"projectName\":null,\n" +
                "                    \"priorityFlag\":\"1\",\n" +
                "                    \"priorityMessage\":null,\n" +
                "                    \"isAccredit\":null,\n" +
                "                    \"accreditType\":null,\n" +
                "                    \"accreditDate\":null,\n" +
                "                    \"bankFlowNo\":\"OR32048200201125400000514\",\n" +
                "                    \"sealNum\":null,\n" +
                "                    \"policyNo\":\"PZIP202033016000000038\",\n" +
                "                    \"classify\":\"A\",\n" +
                "                    \"overSeas\":\"0\",\n" +
                "                    \"isClaim\":null,\n" +
                "                    \"isCondition\":null,\n" +
                "                    \"unifiedInsurance\":\"0\",\n" +
                "                    \"electronicEmail\":null,\n" +
                "                    \"isRenewalTeam\":null,\n" +
                "                    \"keyAccountCode\":null,\n" +
                "                    \"isRenewal\":null,\n" +
                "                    \"isGIvesff\":null,\n" +
                "                    \"isStatistics\":null,\n" +
                "                    \"isInsureRate\":null,\n" +
                "                    \"busiAccountType\":null,\n" +
                "                    \"isPStage\":null,\n" +
                "                    \"visaCode\":null,\n" +
                "                    \"visaPrintCode\":null,\n" +
                "                    \"visaNo\":null,\n" +
                "                    \"isVisaCancel\":null,\n" +
                "                    \"internetCode\":null,\n" +
                "                    \"isPoverty\":null,\n" +
                "                    \"isTargetedPoverty\":null,\n" +
                "                    \"coMakecom\":null,\n" +
                "                    \"coOperatorcode\":null,\n" +
                "                    \"inputType\":null,\n" +
                "                    \"deliverFlag\":null,\n" +
                "                    \"deliverType\":null,\n" +
                "                    \"addressee\":null,\n" +
                "                    \"deliverTel\":null,\n" +
                "                    \"deliverAddr\":null,\n" +
                "                    \"isVsCard\":null,\n" +
                "                    \"subinformation\":null,\n" +
                "                    \"isRapidCalPremium\":null,\n" +
                "                    \"externalPayFlag\":null,\n" +
                "                    \"ownerFlag\":null,\n" +
                "                    \"signTag\":null,\n" +
                "                    \"signState\":null,\n" +
                "                    \"transFlag\":null,\n" +
                "                    \"invokeFlag\":null,\n" +
                "                    \"invoiceCode\":null,\n" +
                "                    \"reviewerName\":null,\n" +
                "                    \"receivableFlag\":null,\n" +
                "                    \"internationalFlag\":null,\n" +
                "                    \"policyFactorFlag\":null,\n" +
                "                    \"recallFlag\":null\n" +
                "                }\n" +
                "            ],\n" +
                "            \"Coupon\":null,\n" +
                "            \"InsuredCataLists\":[\n" +
                "\n" +
                "            ]\n" +
                "        }\n" +
                "    }\n" +
                "}";
        JSONObject ss = JSONObject.parseObject(str);
        List<Object> limits = getJsonFieldValue(ss, "prpPhead");
        System.out.println(limits);
    }
}
