package com.coco.tools;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coco.tools.Test;
import java.util.Iterator;
import java.util.Map;

public class CompareFun {
    public static void main(String[] args) {
        String Jsonstr = "{\n" +
                "    \"status\": 0,\n" +
                "    \"statusText\": \"Success\",\n" +
                "    \"data\": {\n" +
                "        \"status\": \"1\",\n" +
                "        \"message\": null,\n" +
                "        \"bisNo\": \"TEAA202033016000001679\",\n" +
                "        \"PolicyMain\": {\n" +
                "            \"proposalNo\": \"\",\n" +
                "            \"policyNo\": \"\",\n" +
                "            \"classCode\": \"06\",\n" +
                "            \"riskCode\": \"EAA\",\n" +
                "            \"projectCode\": \"\",\n" +
                "            \"contractNo\": \"\",\n" +
                "            \"policySort\": \"2\",\n" +
                "            \"businessNature\": \"5\",\n" +
                "            \"language\": \"C\",\n" +
                "            \"policyType\": \"01\",\n" +
                "            \"agriFlag\": \"0\",\n" +
                "            \"operateDate\": \"2020-10-29 17:41:37\",\n" +
                "            \"startDate\": \"2020-10-30 17:41:37\",\n" +
                "            \"endDate\": \"2021-10-29 17:41:37\",\n" +
                "            \"startHour\": 0,\n" +
                "            \"endHour\": 24,\n" +
                "            \"disRate\": 0.0000,\n" +
                "            \"sumValue\": 400.00,\n" +
                "            \"sumAmount\": 1757000.00,\n" +
                "            \"sumDiscount\": 0.00,\n" +
                "            \"sumPremiumB4Discount\": null,\n" +
                "            \"couponAmount\": null,\n" +
                "            \"couponPremium\": null,\n" +
                "            \"minPremium\": null,\n" +
                "            \"sumPremium\": 400.00,\n" +
                "            \"sumSubPrem\": 145.00,\n" +
                "            \"sumQuantity\": 1,\n" +
                "            \"policyCount\": null,\n" +
                "            \"judicalScope\": \"01\",\n" +
                "            \"argueSolution\": \"1\",\n" +
                "            \"arbitBoardName\": \"\",\n" +
                "            \"payTimes\": 1,\n" +
                "            \"makeCom\": \"32000000\",\n" +
                "            \"operateSite\": null,\n" +
                "            \"comCode\": \"\",\n" +
                "            \"handlerCode\": \"\",\n" +
                "            \"handler1Code\": \"\",\n" +
                "            \"checkFlag\": \"0\",\n" +
                "            \"checkUpCode\": \"\",\n" +
                "            \"checkOpinion\": null,\n" +
                "            \"underWriteCode\": \"\",\n" +
                "            \"underWriteName\": \"\",\n" +
                "            \"operatorCode\": \"32000000\",\n" +
                "            \"inputTime\": \"2020-10-29 17:41:37\",\n" +
                "            \"underWriteEndDate\": null,\n" +
                "            \"statisticsYM\": null,\n" +
                "            \"agentCode\": \"\",\n" +
                "            \"coinsFlag\": \"00\",\n" +
                "            \"reinsFlag\": \"0\",\n" +
                "            \"allinsFlag\": \"0\",\n" +
                "            \"underWriteFlag\": \"0\",\n" +
                "            \"jfeeFlag\": \"0\",\n" +
                "            \"inputFlag\": \"0\",\n" +
                "            \"undwrtSubmitDate\": \"2020-09-14\",\n" +
                "            \"othFlag\": \"000000YY00\",\n" +
                "            \"remark\": \"\",\n" +
                "            \"checkCode\": null,\n" +
                "            \"flag\": \"0\",\n" +
                "            \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "            \"operateTimeForHis\": \"2020-09-14 17:04:32\",\n" +
                "            \"payMode\": \"2\",\n" +
                "            \"payCode\": null,\n" +
                "            \"crossFlag\": \"0\",\n" +
                "            \"sumTaxFee\": 14.44,\n" +
                "            \"sumNetPremium\": 385.56,\n" +
                "            \"prePremium\": 0.00,\n" +
                "            \"pkey\": \"TEAA202033016000001679\",\n" +
                "            \"tkey\": \"2020-09-14 17:04:28\",\n" +
                "            \"currency\": \"CNY\",\n" +
                "            \"dmFlag\": \"A\",\n" +
                "            \"handler1Code_uni\": \"1194130200\",\n" +
                "            \"handlerCode_uni\": \"1194130200\",\n" +
                "            \"isAutoePolicy\": null,\n" +
                "            \"salesCode\": null,\n" +
                "            \"personOri\": \"9\",\n" +
                "            \"productCode\": \"SEAA0003\",\n" +
                "            \"productName\": null,\n" +
                "            \"approverCode\": \"83314677\",\n" +
                "            \"pureRate\": 0.0000,\n" +
                "            \"discount\": 0.000000,\n" +
                "            \"insuredCount\": null,\n" +
                "            \"auditNo\": null,\n" +
                "            \"auditNoToE\": null,\n" +
                "            \"crossSellType\": null,\n" +
                "            \"inputSumPremium\": null,\n" +
                "            \"prpCmainAccs\": [],\n" +
                "            \"prpCmainExts\": [],\n" +
                "            \"prpCmainBonds\": [],\n" +
                "            \"prpCmainCredits\": [],\n" +
                "            \"prpCextendInfos\": [],\n" +
                "            \"prpCmainAirLines\": [],\n" +
                "            \"prpCcommissions\": [],\n" +
                "            \"prpCcoeffs\": [],\n" +
                "            \"prpCmainAgris\": [],\n" +
                "            \"prpCprojects\": [],\n" +
                "            \"prpCprofitFactors\": [],\n" +
                "            \"prpCitemCreditOths\": [],\n" +
                "            \"prpCclauseplans\": [],\n" +
                "            \"prpCpayeeAccounts\": [],\n" +
                "            \"prpCinsuredCreditInvests\": [],\n" +
                "            \"actualProduct\": null,\n" +
                "            \"prpCmainExtraVo\": null,\n" +
                "            \"Employees\": [],\n" +
                "            \"Props\": [],\n" +
                "            \"AgentDetails\": [],\n" +
                "            \"shipDrivers\": [],\n" +
                "            \"Drivers\": [],\n" +
                "            \"Addresses\": [],\n" +
                "            \"InsuredIdvLists\": [],\n" +
                "            \"Crosses\": [],\n" +
                "            \"Subs\": [],\n" +
                "            \"Agents\": [],\n" +
                "            \"Constructs\": [],\n" +
                "            \"Itemkinds\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"itemKindNo\": 1\n" +
                "                    },\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"familyNo\": 1,\n" +
                "                    \"familyName\": null,\n" +
                "                    \"projectCode\": \"暂时写死\",\n" +
                "                    \"clauseCode\": \"060037\",\n" +
                "                    \"clauseName\": \"营运交通工具乘客意外伤害保险条款\",\n" +
                "                    \"kindCode\": \"060006\",\n" +
                "                    \"kindName\": \"在飞机中因意外伤害造成的身故、残疾\",\n" +
                "                    \"itemNo\": 2,\n" +
                "                    \"itemCode\": \"      \",\n" +
                "                    \"itemDetailName\": null,\n" +
                "                    \"groupNo\": 2,\n" +
                "                    \"modeCode\": \"EAA11002qo\",\n" +
                "                    \"modeName\": \"个人综合意外险B全年（DS）001天至366天\",\n" +
                "                    \"startDate\": null,\n" +
                "                    \"startHour\": null,\n" +
                "                    \"endDate\": null,\n" +
                "                    \"endHour\": null,\n" +
                "                    \"model\": null,\n" +
                "                    \"buyDate\": null,\n" +
                "                    \"addressNo\": null,\n" +
                "                    \"calculateFlag\": \"1\",\n" +
                "                    \"currency\": \"CNY\",\n" +
                "                    \"unitAmount\": 500000.00,\n" +
                "                    \"quantity\": 1,\n" +
                "                    \"unit\": \"1\",\n" +
                "                    \"value\": null,\n" +
                "                    \"amount\": 500000.00,\n" +
                "                    \"ratePeriod\": null,\n" +
                "                    \"rate\": 0.07000000000,\n" +
                "                    \"shortRateFlag\": \"3\",\n" +
                "                    \"shortRate\": 100.0000,\n" +
                "                    \"prePremium\": null,\n" +
                "                    \"calPremium\": 35.00,\n" +
                "                    \"basePremium\": null,\n" +
                "                    \"benchMarkPremium\": null,\n" +
                "                    \"discount\": 1.000000,\n" +
                "                    \"adjustRate\": null,\n" +
                "                    \"unitPremium\": 35.00,\n" +
                "                    \"premiumB4Discount\": null,\n" +
                "                    \"premium\": 35.00,\n" +
                "                    \"deductibleRate\": null,\n" +
                "                    \"deductible\": null,\n" +
                "                    \"taxFee\": 1.98,\n" +
                "                    \"taxFee_ys\": null,\n" +
                "                    \"taxFee_gb\": 0.00,\n" +
                "                    \"taxFee_lb\": 0.00,\n" +
                "                    \"netPremium\": 33.02,\n" +
                "                    \"allTaxFee\": 1.98,\n" +
                "                    \"allNetPremium\": 33.02,\n" +
                "                    \"taxRate\": 6.00,\n" +
                "                    \"taxFlag\": \"2\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\",\n" +
                "                    \"prpCprofits\": [],\n" +
                "                    \"iscalculateFlag\": null,\n" +
                "                    \"userCount\": null,\n" +
                "                    \"pack\": null,\n" +
                "                    \"firstLevel\": null,\n" +
                "                    \"methodType\": null,\n" +
                "                    \"insuredQuantity\": null,\n" +
                "                    \"clauseFlag\": \"1\",\n" +
                "                    \"prpCitemKindTaxFees\": [],\n" +
                "                    \"ItemKindDetails\": []\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"itemKindNo\": 2\n" +
                "                    },\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"familyNo\": 1,\n" +
                "                    \"familyName\": null,\n" +
                "                    \"projectCode\": \"暂时写死\",\n" +
                "                    \"clauseCode\": \"060047\",\n" +
                "                    \"clauseName\": \"意外伤害保险条款\",\n" +
                "                    \"kindCode\": \"060066\",\n" +
                "                    \"kindName\": \"意外身故、残疾给付\",\n" +
                "                    \"itemNo\": 2,\n" +
                "                    \"itemCode\": \"      \",\n" +
                "                    \"itemDetailName\": null,\n" +
                "                    \"groupNo\": 2,\n" +
                "                    \"modeCode\": \"EAA11002qo\",\n" +
                "                    \"modeName\": \"个人综合意外险B全年（DS）001天至366天\",\n" +
                "                    \"startDate\": null,\n" +
                "                    \"startHour\": null,\n" +
                "                    \"endDate\": null,\n" +
                "                    \"endHour\": null,\n" +
                "                    \"model\": null,\n" +
                "                    \"buyDate\": null,\n" +
                "                    \"addressNo\": null,\n" +
                "                    \"calculateFlag\": \"1\",\n" +
                "                    \"currency\": \"CNY\",\n" +
                "                    \"unitAmount\": 300000.00,\n" +
                "                    \"quantity\": 1,\n" +
                "                    \"unit\": \"1\",\n" +
                "                    \"value\": null,\n" +
                "                    \"amount\": 300000.00,\n" +
                "                    \"ratePeriod\": null,\n" +
                "                    \"rate\": 0.63333333000,\n" +
                "                    \"shortRateFlag\": \"3\",\n" +
                "                    \"shortRate\": 100.0000,\n" +
                "                    \"prePremium\": null,\n" +
                "                    \"calPremium\": 190.00,\n" +
                "                    \"basePremium\": null,\n" +
                "                    \"benchMarkPremium\": null,\n" +
                "                    \"discount\": 1.000000,\n" +
                "                    \"adjustRate\": null,\n" +
                "                    \"unitPremium\": 190.00,\n" +
                "                    \"premiumB4Discount\": null,\n" +
                "                    \"premium\": 190.00,\n" +
                "                    \"deductibleRate\": null,\n" +
                "                    \"deductible\": null,\n" +
                "                    \"taxFee\": 10.75,\n" +
                "                    \"taxFee_ys\": null,\n" +
                "                    \"taxFee_gb\": 0.00,\n" +
                "                    \"taxFee_lb\": 0.00,\n" +
                "                    \"netPremium\": 179.25,\n" +
                "                    \"allTaxFee\": 10.75,\n" +
                "                    \"allNetPremium\": 179.25,\n" +
                "                    \"taxRate\": 6.00,\n" +
                "                    \"taxFlag\": \"2\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\",\n" +
                "                    \"prpCprofits\": [],\n" +
                "                    \"iscalculateFlag\": null,\n" +
                "                    \"userCount\": null,\n" +
                "                    \"pack\": null,\n" +
                "                    \"firstLevel\": null,\n" +
                "                    \"methodType\": null,\n" +
                "                    \"insuredQuantity\": null,\n" +
                "                    \"clauseFlag\": \"1\",\n" +
                "                    \"prpCitemKindTaxFees\": [],\n" +
                "                    \"ItemKindDetails\": []\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"itemKindNo\": 3\n" +
                "                    },\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"familyNo\": 1,\n" +
                "                    \"familyName\": null,\n" +
                "                    \"projectCode\": \"暂时写死\",\n" +
                "                    \"clauseCode\": \"070059\",\n" +
                "                    \"clauseName\": \"附加意外伤害医疗保险条款（2009版）\",\n" +
                "                    \"kindCode\": \"070050\",\n" +
                "                    \"kindName\": \"意外医疗费用补偿\",\n" +
                "                    \"itemNo\": 2,\n" +
                "                    \"itemCode\": \"      \",\n" +
                "                    \"itemDetailName\": null,\n" +
                "                    \"groupNo\": 2,\n" +
                "                    \"modeCode\": \"EAA11002qo\",\n" +
                "                    \"modeName\": \"个人综合意外险B全年（DS）001天至366天\",\n" +
                "                    \"startDate\": null,\n" +
                "                    \"startHour\": null,\n" +
                "                    \"endDate\": null,\n" +
                "                    \"endHour\": null,\n" +
                "                    \"model\": null,\n" +
                "                    \"buyDate\": null,\n" +
                "                    \"addressNo\": null,\n" +
                "                    \"calculateFlag\": \"1\",\n" +
                "                    \"currency\": \"CNY\",\n" +
                "                    \"unitAmount\": 30000.00,\n" +
                "                    \"quantity\": 1,\n" +
                "                    \"unit\": \"1\",\n" +
                "                    \"value\": null,\n" +
                "                    \"amount\": 30000.00,\n" +
                "                    \"ratePeriod\": null,\n" +
                "                    \"rate\": 3.33333333000,\n" +
                "                    \"shortRateFlag\": \"3\",\n" +
                "                    \"shortRate\": 100.0000,\n" +
                "                    \"prePremium\": null,\n" +
                "                    \"calPremium\": 100.00,\n" +
                "                    \"basePremium\": null,\n" +
                "                    \"benchMarkPremium\": null,\n" +
                "                    \"discount\": 1.000000,\n" +
                "                    \"adjustRate\": null,\n" +
                "                    \"unitPremium\": 100.00,\n" +
                "                    \"premiumB4Discount\": null,\n" +
                "                    \"premium\": 100.00,\n" +
                "                    \"deductibleRate\": null,\n" +
                "                    \"deductible\": null,\n" +
                "                    \"taxFee\": 0.00,\n" +
                "                    \"taxFee_ys\": null,\n" +
                "                    \"taxFee_gb\": 0.00,\n" +
                "                    \"taxFee_lb\": 0.00,\n" +
                "                    \"netPremium\": 100.00,\n" +
                "                    \"allTaxFee\": 0.00,\n" +
                "                    \"allNetPremium\": 100.00,\n" +
                "                    \"taxRate\": 0.00,\n" +
                "                    \"taxFlag\": \"0\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\",\n" +
                "                    \"prpCprofits\": [],\n" +
                "                    \"iscalculateFlag\": null,\n" +
                "                    \"userCount\": null,\n" +
                "                    \"pack\": null,\n" +
                "                    \"firstLevel\": null,\n" +
                "                    \"methodType\": null,\n" +
                "                    \"insuredQuantity\": null,\n" +
                "                    \"clauseFlag\": \"2\",\n" +
                "                    \"prpCitemKindTaxFees\": [],\n" +
                "                    \"ItemKindDetails\": []\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"itemKindNo\": 4\n" +
                "                    },\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"familyNo\": 1,\n" +
                "                    \"familyName\": null,\n" +
                "                    \"projectCode\": \"暂时写死\",\n" +
                "                    \"clauseCode\": \"070060\",\n" +
                "                    \"clauseName\": \"附加意外伤害住院津贴保险条款（2009版）\",\n" +
                "                    \"kindCode\": \"070052\",\n" +
                "                    \"kindName\": \"意外住院津贴\",\n" +
                "                    \"itemNo\": 2,\n" +
                "                    \"itemCode\": \"      \",\n" +
                "                    \"itemDetailName\": null,\n" +
                "                    \"groupNo\": 2,\n" +
                "                    \"modeCode\": \"EAA11002qo\",\n" +
                "                    \"modeName\": \"个人综合意外险B全年（DS）001天至366天\",\n" +
                "                    \"startDate\": null,\n" +
                "                    \"startHour\": null,\n" +
                "                    \"endDate\": null,\n" +
                "                    \"endHour\": null,\n" +
                "                    \"model\": null,\n" +
                "                    \"buyDate\": null,\n" +
                "                    \"addressNo\": null,\n" +
                "                    \"calculateFlag\": \"1\",\n" +
                "                    \"currency\": \"CNY\",\n" +
                "                    \"unitAmount\": 27000.00,\n" +
                "                    \"quantity\": 1,\n" +
                "                    \"unit\": \"1\",\n" +
                "                    \"value\": null,\n" +
                "                    \"amount\": 27000.00,\n" +
                "                    \"ratePeriod\": null,\n" +
                "                    \"rate\": 1.66666667000,\n" +
                "                    \"shortRateFlag\": \"3\",\n" +
                "                    \"shortRate\": 100.0000,\n" +
                "                    \"prePremium\": null,\n" +
                "                    \"calPremium\": 45.00,\n" +
                "                    \"basePremium\": null,\n" +
                "                    \"benchMarkPremium\": null,\n" +
                "                    \"discount\": 1.000000,\n" +
                "                    \"adjustRate\": null,\n" +
                "                    \"unitPremium\": 45.00,\n" +
                "                    \"premiumB4Discount\": null,\n" +
                "                    \"premium\": 45.00,\n" +
                "                    \"deductibleRate\": null,\n" +
                "                    \"deductible\": null,\n" +
                "                    \"taxFee\": 0.00,\n" +
                "                    \"taxFee_ys\": null,\n" +
                "                    \"taxFee_gb\": 0.00,\n" +
                "                    \"taxFee_lb\": 0.00,\n" +
                "                    \"netPremium\": 45.00,\n" +
                "                    \"allTaxFee\": 0.00,\n" +
                "                    \"allNetPremium\": 45.00,\n" +
                "                    \"taxRate\": 0.00,\n" +
                "                    \"taxFlag\": \"0\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\",\n" +
                "                    \"prpCprofits\": [],\n" +
                "                    \"iscalculateFlag\": null,\n" +
                "                    \"userCount\": null,\n" +
                "                    \"pack\": null,\n" +
                "                    \"firstLevel\": null,\n" +
                "                    \"methodType\": null,\n" +
                "                    \"insuredQuantity\": null,\n" +
                "                    \"clauseFlag\": \"2\",\n" +
                "                    \"prpCitemKindTaxFees\": [],\n" +
                "                    \"ItemKindDetails\": []\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"itemKindNo\": 5\n" +
                "                    },\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"familyNo\": 1,\n" +
                "                    \"familyName\": null,\n" +
                "                    \"projectCode\": \"暂时写死\",\n" +
                "                    \"clauseCode\": \"060037\",\n" +
                "                    \"clauseName\": \"营运交通工具乘客意外伤害保险条款\",\n" +
                "                    \"kindCode\": \"060014\",\n" +
                "                    \"kindName\": \"在火车中因意外伤害造成的身故、残疾\",\n" +
                "                    \"itemNo\": 2,\n" +
                "                    \"itemCode\": \"      \",\n" +
                "                    \"itemDetailName\": null,\n" +
                "                    \"groupNo\": 2,\n" +
                "                    \"modeCode\": \"EAA11002qo\",\n" +
                "                    \"modeName\": \"个人综合意外险B全年（DS）001天至366天\",\n" +
                "                    \"startDate\": null,\n" +
                "                    \"startHour\": null,\n" +
                "                    \"endDate\": null,\n" +
                "                    \"endHour\": null,\n" +
                "                    \"model\": null,\n" +
                "                    \"buyDate\": null,\n" +
                "                    \"addressNo\": null,\n" +
                "                    \"calculateFlag\": \"1\",\n" +
                "                    \"currency\": \"CNY\",\n" +
                "                    \"unitAmount\": 300000.00,\n" +
                "                    \"quantity\": 1,\n" +
                "                    \"unit\": \"1\",\n" +
                "                    \"value\": null,\n" +
                "                    \"amount\": 300000.00,\n" +
                "                    \"ratePeriod\": null,\n" +
                "                    \"rate\": 0.03333333000,\n" +
                "                    \"shortRateFlag\": \"3\",\n" +
                "                    \"shortRate\": 100.0000,\n" +
                "                    \"prePremium\": null,\n" +
                "                    \"calPremium\": 10.00,\n" +
                "                    \"basePremium\": null,\n" +
                "                    \"benchMarkPremium\": null,\n" +
                "                    \"discount\": 1.000000,\n" +
                "                    \"adjustRate\": null,\n" +
                "                    \"unitPremium\": 10.00,\n" +
                "                    \"premiumB4Discount\": null,\n" +
                "                    \"premium\": 10.00,\n" +
                "                    \"deductibleRate\": null,\n" +
                "                    \"deductible\": null,\n" +
                "                    \"taxFee\": 0.57,\n" +
                "                    \"taxFee_ys\": null,\n" +
                "                    \"taxFee_gb\": 0.00,\n" +
                "                    \"taxFee_lb\": 0.00,\n" +
                "                    \"netPremium\": 9.43,\n" +
                "                    \"allTaxFee\": 0.57,\n" +
                "                    \"allNetPremium\": 9.43,\n" +
                "                    \"taxRate\": 6.00,\n" +
                "                    \"taxFlag\": \"2\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\",\n" +
                "                    \"prpCprofits\": [],\n" +
                "                    \"iscalculateFlag\": null,\n" +
                "                    \"userCount\": null,\n" +
                "                    \"pack\": null,\n" +
                "                    \"firstLevel\": null,\n" +
                "                    \"methodType\": null,\n" +
                "                    \"insuredQuantity\": null,\n" +
                "                    \"clauseFlag\": \"1\",\n" +
                "                    \"prpCitemKindTaxFees\": [],\n" +
                "                    \"ItemKindDetails\": []\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"itemKindNo\": 6\n" +
                "                    },\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"familyNo\": 1,\n" +
                "                    \"familyName\": null,\n" +
                "                    \"projectCode\": \"暂时写死\",\n" +
                "                    \"clauseCode\": \"060037\",\n" +
                "                    \"clauseName\": \"营运交通工具乘客意外伤害保险条款\",\n" +
                "                    \"kindCode\": \"060037\",\n" +
                "                    \"kindName\": \"在轮船中因意外伤害造成的身故、残疾\",\n" +
                "                    \"itemNo\": 2,\n" +
                "                    \"itemCode\": \"      \",\n" +
                "                    \"itemDetailName\": null,\n" +
                "                    \"groupNo\": 2,\n" +
                "                    \"modeCode\": \"EAA11002qo\",\n" +
                "                    \"modeName\": \"个人综合意外险B全年（DS）001天至366天\",\n" +
                "                    \"startDate\": null,\n" +
                "                    \"startHour\": null,\n" +
                "                    \"endDate\": null,\n" +
                "                    \"endHour\": null,\n" +
                "                    \"model\": null,\n" +
                "                    \"buyDate\": null,\n" +
                "                    \"addressNo\": null,\n" +
                "                    \"calculateFlag\": \"1\",\n" +
                "                    \"currency\": \"CNY\",\n" +
                "                    \"unitAmount\": 300000.00,\n" +
                "                    \"quantity\": 1,\n" +
                "                    \"unit\": \"1\",\n" +
                "                    \"value\": null,\n" +
                "                    \"amount\": 300000.00,\n" +
                "                    \"ratePeriod\": null,\n" +
                "                    \"rate\": 0.03333333000,\n" +
                "                    \"shortRateFlag\": \"3\",\n" +
                "                    \"shortRate\": 100.0000,\n" +
                "                    \"prePremium\": null,\n" +
                "                    \"calPremium\": 10.00,\n" +
                "                    \"basePremium\": null,\n" +
                "                    \"benchMarkPremium\": null,\n" +
                "                    \"discount\": 1.000000,\n" +
                "                    \"adjustRate\": null,\n" +
                "                    \"unitPremium\": 10.00,\n" +
                "                    \"premiumB4Discount\": null,\n" +
                "                    \"premium\": 10.00,\n" +
                "                    \"deductibleRate\": null,\n" +
                "                    \"deductible\": null,\n" +
                "                    \"taxFee\": 0.57,\n" +
                "                    \"taxFee_ys\": null,\n" +
                "                    \"taxFee_gb\": 0.00,\n" +
                "                    \"taxFee_lb\": 0.00,\n" +
                "                    \"netPremium\": 9.43,\n" +
                "                    \"allTaxFee\": 0.57,\n" +
                "                    \"allNetPremium\": 9.43,\n" +
                "                    \"taxRate\": 6.00,\n" +
                "                    \"taxFlag\": \"2\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\",\n" +
                "                    \"prpCprofits\": [],\n" +
                "                    \"iscalculateFlag\": null,\n" +
                "                    \"userCount\": null,\n" +
                "                    \"pack\": null,\n" +
                "                    \"firstLevel\": null,\n" +
                "                    \"methodType\": null,\n" +
                "                    \"insuredQuantity\": null,\n" +
                "                    \"clauseFlag\": \"1\",\n" +
                "                    \"prpCitemKindTaxFees\": [],\n" +
                "                    \"ItemKindDetails\": []\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"itemKindNo\": 7\n" +
                "                    },\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"familyNo\": 1,\n" +
                "                    \"familyName\": null,\n" +
                "                    \"projectCode\": \"暂时写死\",\n" +
                "                    \"clauseCode\": \"060037\",\n" +
                "                    \"clauseName\": \"营运交通工具乘客意外伤害保险条款\",\n" +
                "                    \"kindCode\": \"060043\",\n" +
                "                    \"kindName\": \"在汽车中因意外伤害造成的身故、残疾\",\n" +
                "                    \"itemNo\": 2,\n" +
                "                    \"itemCode\": \"      \",\n" +
                "                    \"itemDetailName\": null,\n" +
                "                    \"groupNo\": 2,\n" +
                "                    \"modeCode\": \"EAA11002qo\",\n" +
                "                    \"modeName\": \"个人综合意外险B全年（DS）001天至366天\",\n" +
                "                    \"startDate\": null,\n" +
                "                    \"startHour\": null,\n" +
                "                    \"endDate\": null,\n" +
                "                    \"endHour\": null,\n" +
                "                    \"model\": null,\n" +
                "                    \"buyDate\": null,\n" +
                "                    \"addressNo\": null,\n" +
                "                    \"calculateFlag\": \"1\",\n" +
                "                    \"currency\": \"CNY\",\n" +
                "                    \"unitAmount\": 300000.00,\n" +
                "                    \"quantity\": 1,\n" +
                "                    \"unit\": \"1\",\n" +
                "                    \"value\": null,\n" +
                "                    \"amount\": 300000.00,\n" +
                "                    \"ratePeriod\": null,\n" +
                "                    \"rate\": 0.03333333000,\n" +
                "                    \"shortRateFlag\": \"3\",\n" +
                "                    \"shortRate\": 100.0000,\n" +
                "                    \"prePremium\": null,\n" +
                "                    \"calPremium\": 10.00,\n" +
                "                    \"basePremium\": null,\n" +
                "                    \"benchMarkPremium\": null,\n" +
                "                    \"discount\": 1.000000,\n" +
                "                    \"adjustRate\": null,\n" +
                "                    \"unitPremium\": 10.00,\n" +
                "                    \"premiumB4Discount\": null,\n" +
                "                    \"premium\": 10.00,\n" +
                "                    \"deductibleRate\": null,\n" +
                "                    \"deductible\": null,\n" +
                "                    \"taxFee\": 0.57,\n" +
                "                    \"taxFee_ys\": null,\n" +
                "                    \"taxFee_gb\": 0.00,\n" +
                "                    \"taxFee_lb\": 0.00,\n" +
                "                    \"netPremium\": 9.43,\n" +
                "                    \"allTaxFee\": 0.57,\n" +
                "                    \"allNetPremium\": 9.43,\n" +
                "                    \"taxRate\": 6.00,\n" +
                "                    \"taxFlag\": \"2\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\",\n" +
                "                    \"prpCprofits\": [],\n" +
                "                    \"iscalculateFlag\": null,\n" +
                "                    \"userCount\": null,\n" +
                "                    \"pack\": null,\n" +
                "                    \"firstLevel\": null,\n" +
                "                    \"methodType\": null,\n" +
                "                    \"insuredQuantity\": null,\n" +
                "                    \"clauseFlag\": \"1\",\n" +
                "                    \"prpCitemKindTaxFees\": [],\n" +
                "                    \"ItemKindDetails\": []\n" +
                "                }\n" +
                "            ],\n" +
                "            \"Limits\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"limitGrade\": \"2\",\n" +
                "                        \"limitNo\": 3,\n" +
                "                        \"limitType\": \"000050\",\n" +
                "                        \"currency\": \"CNY\"\n" +
                "                    },\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"limitFee\": 80.00,\n" +
                "                    \"calculateFlag\": \"\",\n" +
                "                    \"limitFlag\": \"0\",\n" +
                "                    \"isRecorded\": \"1\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"limitGrade\": \"2\",\n" +
                "                        \"limitNo\": 3,\n" +
                "                        \"limitType\": \"000102\",\n" +
                "                        \"currency\": \"CNY\"\n" +
                "                    },\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"limitFee\": 1000.00,\n" +
                "                    \"calculateFlag\": \"\",\n" +
                "                    \"limitFlag\": \"1\",\n" +
                "                    \"isRecorded\": \"1\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"limitGrade\": \"2\",\n" +
                "                        \"limitNo\": 4,\n" +
                "                        \"limitType\": \"000076\",\n" +
                "                        \"currency\": \"CNY\"\n" +
                "                    },\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"limitFee\": 5.00,\n" +
                "                    \"calculateFlag\": \"\",\n" +
                "                    \"limitFlag\": \"1\",\n" +
                "                    \"isRecorded\": \"0\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"limitGrade\": \"2\",\n" +
                "                        \"limitNo\": 4,\n" +
                "                        \"limitType\": \"000117\",\n" +
                "                        \"currency\": \"CNY\"\n" +
                "                    },\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"limitFee\": 90.00,\n" +
                "                    \"calculateFlag\": \"\",\n" +
                "                    \"limitFlag\": \"0\",\n" +
                "                    \"isRecorded\": \"1\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"limitGrade\": \"2\",\n" +
                "                        \"limitNo\": 4,\n" +
                "                        \"limitType\": \"000161\",\n" +
                "                        \"currency\": \"CNY\"\n" +
                "                    },\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"limitFee\": 60.00,\n" +
                "                    \"calculateFlag\": \"\",\n" +
                "                    \"limitFlag\": \"0\",\n" +
                "                    \"isRecorded\": \"1\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"limitGrade\": \"2\",\n" +
                "                        \"limitNo\": 4,\n" +
                "                        \"limitType\": \"000253\",\n" +
                "                        \"currency\": \"CNY\"\n" +
                "                    },\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"limitFee\": 180.00,\n" +
                "                    \"calculateFlag\": \"\",\n" +
                "                    \"limitFlag\": \"0\",\n" +
                "                    \"isRecorded\": \"1\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"limitGrade\": \"2\",\n" +
                "                        \"limitNo\": 10,\n" +
                "                        \"limitType\": \"000050\",\n" +
                "                        \"currency\": \"CNY\"\n" +
                "                    },\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"limitFee\": 80.00,\n" +
                "                    \"calculateFlag\": \"\",\n" +
                "                    \"limitFlag\": \"0\",\n" +
                "                    \"isRecorded\": \"1\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"limitGrade\": \"2\",\n" +
                "                        \"limitNo\": 10,\n" +
                "                        \"limitType\": \"000102\",\n" +
                "                        \"currency\": \"CNY\"\n" +
                "                    },\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"limitFee\": 1000.00,\n" +
                "                    \"calculateFlag\": \"\",\n" +
                "                    \"limitFlag\": \"1\",\n" +
                "                    \"isRecorded\": \"1\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"limitGrade\": \"2\",\n" +
                "                        \"limitNo\": 11,\n" +
                "                        \"limitType\": \"000076\",\n" +
                "                        \"currency\": \"CNY\"\n" +
                "                    },\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"limitFee\": 5.00,\n" +
                "                    \"calculateFlag\": \"\",\n" +
                "                    \"limitFlag\": \"1\",\n" +
                "                    \"isRecorded\": \"0\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"limitGrade\": \"2\",\n" +
                "                        \"limitNo\": 11,\n" +
                "                        \"limitType\": \"000117\",\n" +
                "                        \"currency\": \"CNY\"\n" +
                "                    },\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"limitFee\": 90.00,\n" +
                "                    \"calculateFlag\": \"\",\n" +
                "                    \"limitFlag\": \"0\",\n" +
                "                    \"isRecorded\": \"1\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"limitGrade\": \"2\",\n" +
                "                        \"limitNo\": 11,\n" +
                "                        \"limitType\": \"000161\",\n" +
                "                        \"currency\": \"CNY\"\n" +
                "                    },\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"limitFee\": 150.00,\n" +
                "                    \"calculateFlag\": \"\",\n" +
                "                    \"limitFlag\": \"0\",\n" +
                "                    \"isRecorded\": \"1\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"limitGrade\": \"2\",\n" +
                "                        \"limitNo\": 11,\n" +
                "                        \"limitType\": \"000253\",\n" +
                "                        \"currency\": \"CNY\"\n" +
                "                    },\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"limitFee\": 180.00,\n" +
                "                    \"calculateFlag\": \"\",\n" +
                "                    \"limitFlag\": \"0\",\n" +
                "                    \"isRecorded\": \"1\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"limitGrade\": \"2\",\n" +
                "                        \"limitNo\": 17,\n" +
                "                        \"limitType\": \"000050\",\n" +
                "                        \"currency\": \"CNY\"\n" +
                "                    },\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"limitFee\": 80.00,\n" +
                "                    \"calculateFlag\": \"\",\n" +
                "                    \"limitFlag\": \"0\",\n" +
                "                    \"isRecorded\": \"1\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"limitGrade\": \"2\",\n" +
                "                        \"limitNo\": 17,\n" +
                "                        \"limitType\": \"000102\",\n" +
                "                        \"currency\": \"CNY\"\n" +
                "                    },\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"limitFee\": 1000.00,\n" +
                "                    \"calculateFlag\": \"\",\n" +
                "                    \"limitFlag\": \"1\",\n" +
                "                    \"isRecorded\": \"1\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"limitGrade\": \"2\",\n" +
                "                        \"limitNo\": 18,\n" +
                "                        \"limitType\": \"000076\",\n" +
                "                        \"currency\": \"CNY\"\n" +
                "                    },\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"limitFee\": 5.00,\n" +
                "                    \"calculateFlag\": \"\",\n" +
                "                    \"limitFlag\": \"1\",\n" +
                "                    \"isRecorded\": \"0\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"limitGrade\": \"2\",\n" +
                "                        \"limitNo\": 18,\n" +
                "                        \"limitType\": \"000117\",\n" +
                "                        \"currency\": \"CNY\"\n" +
                "                    },\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"limitFee\": 90.00,\n" +
                "                    \"calculateFlag\": \"\",\n" +
                "                    \"limitFlag\": \"0\",\n" +
                "                    \"isRecorded\": \"1\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"limitGrade\": \"2\",\n" +
                "                        \"limitNo\": 18,\n" +
                "                        \"limitType\": \"000161\",\n" +
                "                        \"currency\": \"CNY\"\n" +
                "                    },\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"limitFee\": 250.00,\n" +
                "                    \"calculateFlag\": \"\",\n" +
                "                    \"limitFlag\": \"0\",\n" +
                "                    \"isRecorded\": \"1\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"limitGrade\": \"2\",\n" +
                "                        \"limitNo\": 18,\n" +
                "                        \"limitType\": \"000253\",\n" +
                "                        \"currency\": \"CNY\"\n" +
                "                    },\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"limitFee\": 180.00,\n" +
                "                    \"calculateFlag\": \"\",\n" +
                "                    \"limitFlag\": \"0\",\n" +
                "                    \"isRecorded\": \"1\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"Loans\": [],\n" +
                "            \"Engages\": [],\n" +
                "            \"Insureds\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"serialNo\": 1\n" +
                "                    },\n" +
                "                    \"serialNo\": null,\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\",\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"language\": \"C\",\n" +
                "                    \"insuredType\": \"1\",\n" +
                "                    \"insuredCode\": \"8888888888888888\",\n" +
                "                    \"insuredName\": \"哈哈哈\",\n" +
                "                    \"insuredEName\": null,\n" +
                "                    \"aliasName\": null,\n" +
                "                    \"insuredAddress\": null,\n" +
                "                    \"insuredNature\": \"3\",\n" +
                "                    \"insuredFlag\": \"100000000000000000000000000000\",\n" +
                "                    \"unitType\": null,\n" +
                "                    \"appendPrintName\": null,\n" +
                "                    \"insuredIdentity\": null,\n" +
                "                    \"relateSerialNo\": null,\n" +
                "                    \"identifyType\": \"01\",\n" +
                "                    \"identifyNumber\": \"230624199005062313\",\n" +
                "                    \"unifiedSocialCreditCode\": null,\n" +
                "                    \"creditLevel\": null,\n" +
                "                    \"possessNature\": null,\n" +
                "                    \"businessSource\": null,\n" +
                "                    \"businessSort\": null,\n" +
                "                    \"occupationCode\": null,\n" +
                "                    \"educationCode\": null,\n" +
                "                    \"bank\": null,\n" +
                "                    \"accountName\": null,\n" +
                "                    \"account\": null,\n" +
                "                    \"linkerName\": null,\n" +
                "                    \"postAddress\": null,\n" +
                "                    \"postCode\": null,\n" +
                "                    \"phoneNumber\": \"13111111111\",\n" +
                "                    \"faxNumber\": null,\n" +
                "                    \"mobile\": \"13111111111\",\n" +
                "                    \"netAddress\": null,\n" +
                "                    \"email\": null,\n" +
                "                    \"dateValid\": null,\n" +
                "                    \"startDate\": \"2020-09-17 00:00:00\",\n" +
                "                    \"endDate\": \"2021-09-16 00:00:00\",\n" +
                "                    \"benefitFlag\": \"N\",\n" +
                "                    \"benefitRate\": 0.00,\n" +
                "                    \"drivingLicenseNo\": null,\n" +
                "                    \"changelessFlag\": null,\n" +
                "                    \"sex\": \"1\",\n" +
                "                    \"age\": 30,\n" +
                "                    \"marriage\": null,\n" +
                "                    \"driverAddress\": null,\n" +
                "                    \"peccancy\": null,\n" +
                "                    \"acceptLicenseDate\": null,\n" +
                "                    \"receiveLicenseYear\": null,\n" +
                "                    \"drivingYears\": null,\n" +
                "                    \"causeTroubleTimes\": null,\n" +
                "                    \"awardLicenseOrgan\": null,\n" +
                "                    \"drivingCarType\": null,\n" +
                "                    \"countryCode\": \"中国\",\n" +
                "                    \"versionNo\": null,\n" +
                "                    \"auditstatus\": null,\n" +
                "                    \"flag\": null,\n" +
                "                    \"warningFlag\": null,\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"blackFlag\": null,\n" +
                "                    \"importSerialNo\": null,\n" +
                "                    \"groupCode\": null,\n" +
                "                    \"groupName\": null,\n" +
                "                    \"dweller\": \"1\",\n" +
                "                    \"customerLevel\": null,\n" +
                "                    \"insuredPYName\": null,\n" +
                "                    \"groupNo\": null,\n" +
                "                    \"itemNo\": null,\n" +
                "                    \"importFlag\": null,\n" +
                "                    \"smsFlag\": null,\n" +
                "                    \"emailFlag\": null,\n" +
                "                    \"sendPhone\": \"13111111111\",\n" +
                "                    \"sendEmail\": \"\",\n" +
                "                    \"subPolicyNo\": null,\n" +
                "                    \"socialSecurityNo\": null,\n" +
                "                    \"electronicflag\": \"1\",\n" +
                "                    \"insuredSort\": null,\n" +
                "                    \"isHealthSurvey\": null,\n" +
                "                    \"InsuredNatures\": [],\n" +
                "                    \"InsuredArtifs\": []\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"serialNo\": 2\n" +
                "                    },\n" +
                "                    \"serialNo\": null,\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\",\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"language\": \"C\",\n" +
                "                    \"insuredType\": \"1\",\n" +
                "                    \"insuredCode\": \"8888888888888888\",\n" +
                "                    \"insuredName\": \"哈哈哈\",\n" +
                "                    \"insuredEName\": null,\n" +
                "                    \"aliasName\": null,\n" +
                "                    \"insuredAddress\": null,\n" +
                "                    \"insuredNature\": \"3\",\n" +
                "                    \"insuredFlag\": \"010000000000000000000000000000\",\n" +
                "                    \"unitType\": null,\n" +
                "                    \"appendPrintName\": null,\n" +
                "                    \"insuredIdentity\": \"0\",\n" +
                "                    \"relateSerialNo\": null,\n" +
                "                    \"identifyType\": \"01\",\n" +
                "                    \"identifyNumber\": \"230624199005062313\",\n" +
                "                    \"unifiedSocialCreditCode\": null,\n" +
                "                    \"creditLevel\": null,\n" +
                "                    \"possessNature\": null,\n" +
                "                    \"businessSource\": null,\n" +
                "                    \"businessSort\": null,\n" +
                "                    \"occupationCode\": \"050214\",\n" +
                "                    \"educationCode\": null,\n" +
                "                    \"bank\": null,\n" +
                "                    \"accountName\": null,\n" +
                "                    \"account\": null,\n" +
                "                    \"linkerName\": null,\n" +
                "                    \"postAddress\": null,\n" +
                "                    \"postCode\": null,\n" +
                "                    \"phoneNumber\": null,\n" +
                "                    \"faxNumber\": null,\n" +
                "                    \"mobile\": null,\n" +
                "                    \"netAddress\": null,\n" +
                "                    \"email\": null,\n" +
                "                    \"dateValid\": null,\n" +
                "                    \"startDate\": \"2020-09-17 00:00:00\",\n" +
                "                    \"endDate\": \"2021-09-16 00:00:00\",\n" +
                "                    \"benefitFlag\": \"N\",\n" +
                "                    \"benefitRate\": 0.00,\n" +
                "                    \"drivingLicenseNo\": null,\n" +
                "                    \"changelessFlag\": null,\n" +
                "                    \"sex\": \"1\",\n" +
                "                    \"age\": 30,\n" +
                "                    \"marriage\": null,\n" +
                "                    \"driverAddress\": null,\n" +
                "                    \"peccancy\": null,\n" +
                "                    \"acceptLicenseDate\": null,\n" +
                "                    \"receiveLicenseYear\": null,\n" +
                "                    \"drivingYears\": null,\n" +
                "                    \"causeTroubleTimes\": null,\n" +
                "                    \"awardLicenseOrgan\": null,\n" +
                "                    \"drivingCarType\": null,\n" +
                "                    \"countryCode\": \"CHN\",\n" +
                "                    \"versionNo\": null,\n" +
                "                    \"auditstatus\": null,\n" +
                "                    \"flag\": null,\n" +
                "                    \"warningFlag\": null,\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"blackFlag\": null,\n" +
                "                    \"importSerialNo\": null,\n" +
                "                    \"groupCode\": null,\n" +
                "                    \"groupName\": null,\n" +
                "                    \"dweller\": \"1\",\n" +
                "                    \"customerLevel\": null,\n" +
                "                    \"insuredPYName\": null,\n" +
                "                    \"groupNo\": null,\n" +
                "                    \"itemNo\": null,\n" +
                "                    \"importFlag\": null,\n" +
                "                    \"smsFlag\": null,\n" +
                "                    \"emailFlag\": null,\n" +
                "                    \"sendPhone\": null,\n" +
                "                    \"sendEmail\": null,\n" +
                "                    \"subPolicyNo\": null,\n" +
                "                    \"socialSecurityNo\": null,\n" +
                "                    \"electronicflag\": null,\n" +
                "                    \"insuredSort\": null,\n" +
                "                    \"isHealthSurvey\": null,\n" +
                "                    \"InsuredNatures\": [],\n" +
                "                    \"InsuredArtifs\": []\n" +
                "                }\n" +
                "            ],\n" +
                "            \"Coins\": [],\n" +
                "            \"SpecialFacs\": [],\n" +
                "            \"Batches\": [],\n" +
                "            \"Commissions\": [],\n" +
                "            \"Cargos\": [],\n" +
                "            \"Plans\": [],\n" +
                "            \"CoinsDetails\": [],\n" +
                "            \"Liabs\": [],\n" +
                "            \"Confines\": [],\n" +
                "            \"Rations\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"modeCode\": \"2\",\n" +
                "                        \"pkey\": \"TEAA202033016000001679\"\n" +
                "                    },\n" +
                "                    \"modeName\": \"个人综合意外险B全年（DS）001天至366天\",\n" +
                "                    \"planCode\": \"EAA11002qo\",\n" +
                "                    \"serialNo\": 1,\n" +
                "                    \"itinerary\": null,\n" +
                "                    \"sex\": null,\n" +
                "                    \"age\": null,\n" +
                "                    \"occupationCode\": null,\n" +
                "                    \"jobTitle\": null,\n" +
                "                    \"quantity\": 1,\n" +
                "                    \"rationCount\": 1,\n" +
                "                    \"groupDiscount\": null,\n" +
                "                    \"insuredFlag\": null,\n" +
                "                    \"countryCode\": null,\n" +
                "                    \"sickRoomLevel\": null,\n" +
                "                    \"journeyBack\": null,\n" +
                "                    \"journeyEnd\": null,\n" +
                "                    \"journeyStart\": null,\n" +
                "                    \"remark\": null,\n" +
                "                    \"updateFlag\": null,\n" +
                "                    \"flag\": null,\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\",\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"discountType\": null,\n" +
                "                    \"discountMode\": null,\n" +
                "                    \"discountValue\": null,\n" +
                "                    \"unitPremium\": null,\n" +
                "                    \"premiumB4Discount\": null,\n" +
                "                    \"premium\": null,\n" +
                "                    \"planTypeCode\": null\n" +
                "                }\n" +
                "            ],\n" +
                "            \"Items\": [],\n" +
                "            \"Fees\": [],\n" +
                "            \"Renewals\": [],\n" +
                "            \"CargoDetails\": [],\n" +
                "            \"Cprotocols\": [],\n" +
                "            \"Clauses\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"clauseCode\": \"060037\"\n" +
                "                    },\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"clauseName\": \"营运交通工具乘客意外伤害保险条款\",\n" +
                "                    \"clauseDesc\": null,\n" +
                "                    \"flag\": null,\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"clauseCode\": \"060047\"\n" +
                "                    },\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"clauseName\": \"意外伤害保险条款\",\n" +
                "                    \"clauseDesc\": null,\n" +
                "                    \"flag\": null,\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"clauseCode\": \"070059\"\n" +
                "                    },\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"clauseName\": \"附加意外伤害医疗保险条款（2009版）\",\n" +
                "                    \"clauseDesc\": null,\n" +
                "                    \"flag\": null,\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"TEAA202033016000001679\",\n" +
                "                        \"clauseCode\": \"070060\"\n" +
                "                    },\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"riskCode\": \"EAA\",\n" +
                "                    \"clauseName\": \"附加意外伤害住院津贴保险条款（2009版）\",\n" +
                "                    \"clauseDesc\": null,\n" +
                "                    \"flag\": null,\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"Contributions\": [],\n" +
                "            \"Commons\": [\n" +
                "                {\n" +
                "                    \"pkey\": \"TEAA202033016000001679\",\n" +
                "                    \"tkey\": \"2020-09-14 17:04:28\",\n" +
                "                    \"proposalNo\": \"TEAA202033016000001679\",\n" +
                "                    \"specialFlag\": \"0 0\",\n" +
                "                    \"ext1\": null,\n" +
                "                    \"ext2\": \"\",\n" +
                "                    \"ext3\": \"\",\n" +
                "                    \"resourceCode\": null,\n" +
                "                    \"resourceName\": null,\n" +
                "                    \"qualityLevel\": null,\n" +
                "                    \"insertTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"operateTimeForHis\": \"2020-09-14 17:04:28\",\n" +
                "                    \"newBusinessNature\": \"532\",\n" +
                "                    \"scmsAuditNotion\": null,\n" +
                "                    \"pay_method\": null,\n" +
                "                    \"platformProjectCode\": \"WAP000000\",\n" +
                "                    \"handler1Code_uni\": \"1194130200\",\n" +
                "                    \"handlerCode_uni\": \"1194130200\",\n" +
                "                    \"commonFlag\": \"0         0\",\n" +
                "                    \"otherPolicyName\": null,\n" +
                "                    \"groupName\": \"\",\n" +
                "                    \"isHPDriveCus\": \"0\",\n" +
                "                    \"startTime\": \"00:00\",\n" +
                "                    \"endTime\": \"00:00\",\n" +
                "                    \"salesCode\": null,\n" +
                "                    \"electronic\": \"0\",\n" +
                "                    \"electronicTitle\": \"1\",\n" +
                "                    \"electronicPhone\": null,\n" +
                "                    \"socialinsPay\": null,\n" +
                "                    \"socialinsNo\": null,\n" +
                "                    \"projectCode\": null,\n" +
                "                    \"projectName\": null,\n" +
                "                    \"priorityFlag\": null,\n" +
                "                    \"priorityMessage\": null,\n" +
                "                    \"isAccredit\": \"1\",\n" +
                "                    \"accreditType\": \"1\",\n" +
                "                    \"accreditDate\": \"2020-09-14 00:00:00\",\n" +
                "                    \"bankFlowNo\": null,\n" +
                "                    \"sealNum\": null,\n" +
                "                    \"policyNo\": null,\n" +
                "                    \"classify\": null,\n" +
                "                    \"overSeas\": \"0\",\n" +
                "                    \"isClaim\": null,\n" +
                "                    \"isCondition\": null,\n" +
                "                    \"unifiedInsurance\": null,\n" +
                "                    \"electronicEmail\": null,\n" +
                "                    \"isRenewalTeam\": null,\n" +
                "                    \"keyAccountCode\": null,\n" +
                "                    \"isRenewal\": \"0\",\n" +
                "                    \"isGIvesff\": null,\n" +
                "                    \"isStatistics\": null,\n" +
                "                    \"isInsureRate\": null,\n" +
                "                    \"busiAccountType\": null,\n" +
                "                    \"isPStage\": null,\n" +
                "                    \"visaCode\": null,\n" +
                "                    \"visaPrintCode\": null,\n" +
                "                    \"visaNo\": null,\n" +
                "                    \"isVisaCancel\": null,\n" +
                "                    \"internetCode\": null,\n" +
                "                    \"isPoverty\": null,\n" +
                "                    \"isTargetedPoverty\": null,\n" +
                "                    \"coMakecom\": null,\n" +
                "                    \"coOperatorcode\": null,\n" +
                "                    \"inputType\": null,\n" +
                "                    \"deliverFlag\": null,\n" +
                "                    \"deliverType\": null,\n" +
                "                    \"addressee\": null,\n" +
                "                    \"deliverTel\": null,\n" +
                "                    \"deliverAddr\": null,\n" +
                "                    \"isVsCard\": null,\n" +
                "                    \"subinformation\": null,\n" +
                "                    \"isRapidCalPremium\": null,\n" +
                "                    \"externalPayFlag\": null,\n" +
                "                    \"ownerFlag\": null,\n" +
                "                    \"signTag\": null,\n" +
                "                    \"signState\": null,\n" +
                "                    \"transFlag\": null,\n" +
                "                    \"invokeFlag\": null\n" +
                "                }\n" +
                "            ],\n" +
                "            \"Coupon\": null\n" +
                "        }\n" +
                "    }\n" +
                "}";

        JSONObject jsonObject = JSONObject.parseObject(Jsonstr);
        JSONObject data = jsonObject.getJSONObject("data");
        boolean empty = data.isEmpty();
        System.out.println(empty);


        Iterator iterator = data.keySet().iterator();
        while(iterator.hasNext()){
            String key = (String) iterator.next();
            String value = data.getString(key);
            Test test = new Test();
            test.isJson(value);


        }
        }


    }


