package com.bank.dao;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bank.util.MD5;
import com.bank.util.XmlInput;
import com.bank.util.XmlOutput;
import com.bank.util.XmlUtil;
import com.bank.util.XmlVo;
import com.base.utils.DateUtils;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;

public class BankBaseDao implements BankIntf {

    Logger log = Logger.getLogger(this.getClass());

    public String createChildAccount(String accountName, String bankid,
            String centerNo, String currency) throws Exception {
        XmlVo vo = new XmlVo();
        XmlInput input = new XmlInput();
        XmlOutput output = new XmlOutput();
        XmlUtil xmlUtil = new XmlUtil();

        BankDao _bankDao = new BankDao();
        String busino = "DK" + _bankDao.getBankLSH();
        input.setTrxcode("10001");
        input.setBusino(busino);
        input.setOrgno(centerNo);
        // 河源，不要
        // input.setCurrency(currency);
        input.setUsername(accountName);
        input.setRemark("开户");
        input.setBankid(bankid);
        // String
        // rsa=input.getTrxcode()+input.getBusino()+input.getOrgno()+input.getCurrency()+input.getUsername()+input.getRemark()+input.getBankid();
        String rsa = input.getTrxcode() + input.getBusino() + input.getOrgno()
                + input.getUsername() + input.getRemark() + input.getBankid();
        rsa = MD5.MD5Encode(rsa);
        input.setRsa(rsa);
        output.setStatus("--");
        output.setRetcode("--");
        output.setRetmsg("--");
        output.setAccno("--");
        output.setCommseq("--");
        vo.setInput(input);
        vo.setOutput(output);
        String xml = xmlUtil.writeXMLFile(vo);
        String businessId = _bankDao.saveXml(busino, bankid, "10001", xml, "");
        String feedBack = _bankDao.sendXml(xml, bankid, businessId);
        int status = _bankDao.dealBankFeedBack(businessId, feedBack);
        String result = null;
        if (status == 2) {
            XmlOutput backOuput = _bankDao.parseFeedBack(feedBack);
            if (backOuput != null) {
                result = backOuput.getAccno();
            }
        }
        return result;

        // XmlOutput backOuput = null;
        // for(int i = 0 ; i < 20 ; i ++){
        // try{
        // Thread.sleep(1000);//等待1秒
        // BankDao bankDao = new BankDao();
        // backOuput = bankDao.getOutput(businessId);
        // if(backOuput!=null && backOuput.getStatus() !=null){
        // break;
        // }
        // }catch(Exception e){
        // }
        // }

    }

    public String submitChildAccount(String targetId) throws Exception {

        XmlVo vo = new XmlVo();
        XmlInput input = new XmlInput();
        XmlOutput output = new XmlOutput();
        XmlUtil xmlUtil = new XmlUtil();

        DecimalFormat df1 = new DecimalFormat("############0.00");
        BankDao _bankDao = new BankDao();

        ParaMap childNoMap = _bankDao.getSubmitParam(targetId);
        if (childNoMap == null) {
            return null;
        }
        Iterator iter = childNoMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            ParaMap map = (ParaMap) entry.getValue();

            String centerNo = map.getString("centerNo");
            String name = map.getString("name");
            String no = map.getString("no");
            BigDecimal amount = map.getBigDecimal("amount");
            String bankId = map.getString("bankId");
            String billId = map.getString("billId");

            String amoutStr = this.changeMoneyFormat(amount);

            String busino = "DK" + _bankDao.getBankLSH();
            input.setTrxcode("10004");
            input.setBusino(busino);
            input.setTrxcacc(centerNo);
            input.setTrxcname(name);
            input.setAccno(no);
            input.setAmount(amoutStr);
            input.setRemark("子转总");
            input.setBankid(bankId);
            String rsaStr = input.getTrxcode() + input.getBusino()
                    + input.getTrxcacc() + input.getTrxcname()
                    + input.getAccno() + input.getAmount() + input.getRemark()
                    + input.getBankid();
            System.out.println("--->rsaStr=" + rsaStr);
            rsaStr = MD5.MD5Encode(rsaStr);
            input.setRsa("");
            output.setStatus("--");
            output.setRetcode("--");
            output.setRetmsg("--");
            output.setCommseq("--");

            vo.setInput(input);
            vo.setOutput(output);
            String xml = xmlUtil.writeXMLFile(vo);
            String businessId = _bankDao.saveXml(busino, bankId, "10004", xml,
                    billId);

        }

        return null;
    }

    public String submitChildAccount(String centerNo, String name, String no,
            String bankId, String amoutStr, String billId) throws Exception {

        XmlVo vo = new XmlVo();
        XmlInput input = new XmlInput();
        XmlOutput output = new XmlOutput();
        XmlUtil xmlUtil = new XmlUtil();

        DecimalFormat df1 = new DecimalFormat("############0.00");
        BankDao _bankDao = new BankDao();

        String busino = "DK" + _bankDao.getBankLSH();
        input.setTrxcode("10004");
        input.setBusino(busino);
        input.setTrxcacc(centerNo);
        input.setTrxcname(name);
        input.setAccno(no);
        input.setAmount(amoutStr);
        input.setRemark("子转总");
        input.setBankid(bankId);
        String rsaStr = input.getTrxcode() + input.getBusino()
                + input.getTrxcacc() + input.getTrxcname() + input.getAccno()
                + input.getAmount() + input.getRemark() + input.getBankid();
        System.out.println("--->rsaStr=" + rsaStr);
        rsaStr = MD5.MD5Encode(rsaStr);
        input.setRsa(" ");
        output.setStatus("--");
        output.setRetcode("--");
        output.setRetmsg("--");
        output.setCommseq("--");

        vo.setInput(input);
        vo.setOutput(output);
        String xml = xmlUtil.writeXMLFile(vo);
        String businessId = _bankDao.saveXml(busino, bankId, "10004", xml,
                billId);
        String feedBack = _bankDao.sendXml(xml, bankId, businessId);
        int status = _bankDao.dealBankFeedBack(businessId, feedBack);
        String result = null;
        if (status == 2) {
            _bankDao.updateAccountBillStatus("'" + billId + "'");
            XmlOutput backOuput = _bankDao.parseFeedBack(feedBack);
            if (backOuput != null) {
                result = backOuput.getRetmsg();
            }
        }
        return result;
    }

    public String calcInterest(String benjin, String no, String beginDate,
            String endDate, String bankid) throws Exception {

        XmlVo vo = new XmlVo();
        XmlInput input = new XmlInput();
        XmlOutput output = new XmlOutput();
        XmlUtil xmlUtil = new XmlUtil();

        BankDao _bankDao = new BankDao();
        String busino = "DK" + _bankDao.getBankLSH();
        input.setTrxcode("10003");
        input.setBusino(busino);
        input.setBenjin(benjin);
        input.setAccno(no);
        input.setStartdate(beginDate);
        input.setEnddate(endDate);
        input.setRemark("计息");
        input.setBankid(bankid);
        String rsaStr = input.getTrxcode() + input.getBusino()
                + input.getBenjin() + input.getAccno() + input.getStartdate()
                + input.getEnddate() + input.getRemark() + input.getBankid();
        rsaStr = MD5.MD5Encode(rsaStr);
        input.setRsa(" ");
        output.setStatus("--");
        output.setRetcode("--");
        output.setAmount("--");
        output.setRetmsg("--");
        output.setCommseq("--");

        vo.setInput(input);
        vo.setOutput(output);
        String xml = xmlUtil.writeXMLFile(vo);
        String businessId = _bankDao.saveXml(busino, bankid, "10003", xml, "");
        String feedBack = _bankDao.sendXml(xml, bankid, businessId);
        int status = _bankDao.dealBankFeedBack(businessId, feedBack);
        String result = null;
        if (status == 2) {
            XmlOutput backOuput = _bankDao.parseFeedBack(feedBack);
            if (backOuput != null) {
                result = backOuput.getAmount();
            }
        }
        return result;

        // XmlOutput backOuput = null;
        // for(int i = 0 ; i < 15 ; i ++){
        // try{
        // Thread.sleep(1000);//等待1秒
        // backOuput = _bankDao.getOutput(businessId);
        // if(backOuput!=null && backOuput.getStatus() !=null){
        // break;
        // }
        // }catch(Exception e){
        // }
        // }
        //
        // String result = null;
        // if(backOuput== null || backOuput.getStatus()==null){
        // return null;
        // }
        // String _rStatus =backOuput.getStatus();
        // String _retCode=backOuput.getRetcode();
        // String _retmsg = backOuput.getRetmsg();
        // String _amount = backOuput.getAmount();
        // if(("0000".equals(_retCode)&&"00".equals(_rStatus)) ||
        // ("0210".equals(_retCode)&&"00".equals(_rStatus))){
        // result = _amount;
        // }
        // return result;

    }

    public String cancelChildAccount(String accountNo, String bankid,
            String billApplyId) throws Exception {

        XmlVo vo = new XmlVo();
        XmlInput input = new XmlInput();
        XmlOutput output = new XmlOutput();
        XmlUtil xmlUtil = new XmlUtil();

        BankDao _bankDao = new BankDao();
        String busino = "DK" + _bankDao.getBankLSH();
        input.setTrxcode("10005");
        input.setBusino(busino);
        input.setAccno(accountNo);
        input.setRemark("销户");
        input.setBankid(bankid);
        String rsa = input.getTrxcode() + input.getBusino() + input.getAccno()
                + input.getRemark() + input.getBankid();
        rsa = MD5.MD5Encode(rsa);
        input.setRsa(" ");
        output.setStatus("--");
        output.setRetcode("--");
        output.setRetmsg("--");
        output.setCommseq("--");
        vo.setInput(input);
        vo.setOutput(output);
        String xml = xmlUtil.writeXMLFile(vo);
        String businessId = _bankDao.saveXml(busino, bankid, "10005", xml,
                billApplyId);
        String feedBack = _bankDao.sendXml(xml, bankid, businessId);
        int status = _bankDao.dealBankFeedBack(businessId, feedBack);
        String result = null;
        if (status == 2) {
            // 销户成功
            _bankDao.updateBillApplyChildAccountFreed(billApplyId, 1);
            result = "success";
        } else {
            result = "false";
        }
        return result;
        // XmlOutput backOuput = null;
        // for(int i = 0 ; i < 15 ; i ++){
        // try{
        // Thread.sleep(1000);//等待1秒
        // backOuput = _bankDao.getOutput(businessId);
        // if(backOuput!=null && backOuput.getStatus() !=null){
        // break;
        // }
        // }catch(Exception e){
        // }
        // }
        //
        // String result = null;
        // String _rStatus =backOuput.getStatus();
        // String _retCode=backOuput.getRetcode();
        // String _retmsg = backOuput.getRetmsg();
        // if("0000".equals(_retCode)&&"00".equals(_rStatus)){
        // result = "success";
        // }else{
        // result = "false:"+_retmsg;
        // }
        //
        // return result;

    }

    // public String cancelChildAccount(String billApplyId , int businessType)
    // throws Exception{
    // String result = null;
    // BankDao dao = new BankDao();
    // ParaMap applyMap = dao.GetTransAccountBillApply(billApplyId);
    // ParaMap accountMap = dao.getBankAccount(applyMap.getString("account_no"),
    // businessType);
    // if(accountMap.getInt("in_account_mode") == 3){
    // XmlVo vo = new XmlVo();
    // XmlInput input = new XmlInput();
    // XmlOutput output = new XmlOutput();
    // XmlUtil xmlUtil = new XmlUtil();
    //
    // BankDao _bankDao = new BankDao();
    // String busino = "DK"+_bankDao.getBankLSH();
    // input.setTrxcode("10005");
    // input.setBusino(busino);
    // input.setAccno(applyMap.getString("child_account_no"));
    // input.setRemark("销户");
    // input.setBankid(accountMap.getString("bank_no"));
    // String
    // rsa=input.getTrxcode()+input.getBusino()+input.getAccno()+input.getRemark()+input.getBankid();
    // rsa=MD5.MD5Encode(rsa);
    // input.setRsa(rsa);
    // output.setStatus("--");
    // output.setRetcode("--");
    // output.setRetmsg("--");
    // output.setCommseq("--");
    // vo.setInput(input);
    // vo.setOutput(output);
    // String xml = xmlUtil.writeXMLFile(vo);
    // String businessId = _bankDao.saveXml(busino,
    // accountMap.getString("bank_no"), "10005", xml,billApplyId);
    // String feedBack = _bankDao.sendXml(xml, accountMap.getString("bank_no"),
    // businessId);
    // int status = _bankDao.dealBankFeedBack(businessId, feedBack);
    //
    // if(status == 2){
    // //销户成功
    // _bankDao.updateBillApplyChildAccountFreed(billApplyId, 1);
    // }
    // if(status>1){
    // XmlOutput backOuput = _bankDao.parseFeedBack(feedBack);
    // if(backOuput != null ){
    // result = backOuput.getRetmsg();
    // }
    // }
    // }
    // return result;
    //
    //
    // }

    public String dealWith10002(String xml) throws Exception {
        log.info("10002报文：");
        log.info(xml);

        XmlUtil xu = new XmlUtil();
        XmlVo xmlVo = xu.readXML(xml);
        XmlInput input = xmlVo.getInput();
        XmlOutput output = xmlVo.getOutput();

        String busiNo = input.getBusino();
        String bankId = input.getBankid();
        String trxCode = input.getTrxcode();
        String bankid = input.getBankid();
        String draccbank = "无";// StrUtils.isNotNull(input.getDraccbank())?input.getDraccbank():"无";
        String draccsubbank = "无";// StrUtils.isNotNull(input.getDraccsubbank())?input.getDraccsubbank():"无";
        String draccno = input.getDraccno();
        String draccname = input.getDraccname();
        String craccno = input.getCraccno();
        String amountStr = input.getAmount();
        String currency = input.getCurrency();
        currency = StrUtils.isNotNull(currency) ? currency : "CNY";
        String transtime = input.getTranstime();
        String busiid = input.getBusiid();
        draccbank = draccsubbank != null && !"".equals(draccsubbank) ? draccsubbank
                : draccbank;
        draccbank = draccbank == null || "".equals(draccbank) ? "-" : draccbank;
        draccno = draccno == null || "".equals(draccno) ? "-" : draccno;
        draccname = draccname == null || "".equals(draccname) ? "-" : draccname;
        Date inDate = transtime == null || "".equals(transtime) ? DateUtils
                .now() : DateUtils.getDate(transtime.substring(0, 4) + "-"
                + transtime.substring(4, 6) + "-" + transtime.substring(6, 8)
                + " " + transtime.substring(8, 10) + ":"
                + transtime.substring(10, 12) + ":" + transtime.substring(12));
        BigDecimal amount = amountStr == null || "".equals(amountStr) ? new BigDecimal(
                "0") : (new BigDecimal(amountStr)).divide(
                new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);

        BankDao _bankDao = new BankDao();
        // 保存10002报文
        ParaMap businessMap = _bankDao.save10002(busiNo, bankId, trxCode, xml);
        if (businessMap == null || businessMap.get("id") == null) {
            return _bankDao.createFeedback10002(input, "01", "0001", "报文保存失败!");
        }
        String businessId = businessMap.getString("id");
        if (businessMap.getInt("isEnd") == 1) {// 不需要再往下执行了，直接回执
            if ("0021".equals(businessMap.getString("retcode"))) {
                // 之前来帐成功、修改缴款人相关信息（该方法可能在不同的银行有不同的实现）
                _bankDao.updateDealerBy10002(draccbank, draccno, draccname,
                        businessMap.getString("billId"));
            }
            return _bankDao.createFeedback10002(input,
                    businessMap.getString("status"),
                    businessMap.getString("retcode"),
                    businessMap.getString("retmsg"));
        }

        if (craccno == null || "".equals(craccno)) {
            _bankDao.updateBankBusinessStatus(businessId, "", 3);// 处理失败
            return _bankDao.createFeedback10002(input, "01", "0001",
                    "报文缺少贷方账号!");
        }
        // 根据得到是主帐号还是子账号。不支持一个主账号既使用子账号直连直通，又使用主账号直连直通
        int isSub = _bankDao.isSub(bankid, craccno, busiid, currency);// -1无效 0
                                                                      // 主账号 1
                                                                      // 子账号
        System.out.println("****************************");
        System.out.println("isSub=" + isSub);
        System.out.println("****************************");
        if (isSub == -1) {
            _bankDao.updateBankBusinessStatus(businessId, "", 3);// 处理失败
            return _bankDao.createFeedback10002(input, "01", "0001",
                    "报文中的账号找不到所属的银行!");
        }

        // 判断子账号是否正确
        if (isSub == 1) {
            boolean isRight = _bankDao.isRightChildNo(input.getCraccno(),
                    currency, bankid);
            if (!isRight) {
                _bankDao.updateBankBusinessStatus(businessId, "", 3);// 处理失败
                return _bankDao.createFeedback10002(input, "01", "0001",
                        "子账号或币种错误!");
            }
        }

        // 得到入账申请单信息
        String applyId = null;
        String billId = null;
        ParaMap transAccountBillApply = _bankDao.GetTransAccountBillApply(
                isSub, input.getCraccno(), input.getBusiid());
        System.out.println("****************************");
        System.out.println("transAccountBillApply=" + transAccountBillApply);
        System.out.println("****************************");
        // 关联不上竞买申请
        if (transAccountBillApply == null) {
            if (isSub == 1) {
                // 子账号没有关联上入账申请单
                _bankDao.updateBankBusinessStatus(businessId, "", 3);// 处理失败
                return _bankDao.createFeedback10002(input, "01", "0001",
                        "子账号未找到有效地竞买申请!");
            } else {
                // 主账号没有关联上竞买申请
                ParaMap accountMap = new ParaMap();
                ParaMap taMap = _bankDao.getMainAccount(bankid);// 得到主账号信息
                if (accountMap == null) {
                    _bankDao.updateBankBusinessStatus(businessId, "", 3);// 处理失败
                    return _bankDao.createFeedback10002(input, "01", "0001",
                            "未找到有效地竞买申请!");
                } else {
                    accountMap
                            .put("account_bank", taMap.getString("bank_name"));
                    accountMap.put("account_no", taMap.getString("no"));
                    accountMap.put("account_name", taMap.getString("name"));
                    accountMap.put("child_account_bank",
                            taMap.getString("bank_name"));
                    accountMap.put("child_account_no", taMap.getString("no"));
                    accountMap.put("child_account_name",
                            taMap.getString("name"));
                }
                // 保存入账单 状态为录入中
                billId = _bankDao.saveTransBillAccount(accountMap, draccbank,
                        draccno, draccname, amount, inDate, applyId, 0, 0, 0,
                        currency, busiNo);
                _bankDao.updateBankBusinessStatus(businessId, billId, 2);// 处理成功
                return _bankDao.createFeedback10002(input, "00", "0000",
                        "主账号来帐通知成功!");
            }
        }
        applyId = transAccountBillApply.getString("id");
        if (transAccountBillApply.getInt("apply_type") == 0) {
            // 关联的是trans_license
            ParaMap transLicense = _bankDao.GetTableInfo("trans_license",
                    transAccountBillApply.getString("ref_id"));// 得到竞买申请信息
            System.out.println("****************************");
            System.out.println("transLicense=" + transLicense);
            System.out.println("****************************");
            if (transLicense == null) {
                _bankDao.updateBankBusinessStatus(businessId, "", 3);// 处理失败
                return _bankDao.createFeedback10002(input, "01", "0001",
                        "未找到有效地竞买申请!");
            }
            // 得到标的信息
            ParaMap transTarget = _bankDao.GetTableInfo("trans_target",
                    transLicense.getString("target_id"));// 得到标的信息
            System.out.println("****************************");
            System.out.println("transTarget=" + transTarget);
            System.out.println("****************************");
            if (transTarget == null) {
                _bankDao.updateBankBusinessStatus(businessId, "", 3);// 处理失败
                return _bankDao.createFeedback10002(input, "01", "0001",
                        "竞买申请状态错误!");
            }
            // 得到保证金信息
            BigDecimal earnestMoney = null;
            String businessType = transTarget.getString("business_type");
            if (businessType.startsWith("501")) {
                earnestMoney = transTarget.getBigDecimal("earnest_money");
            } else {
                earnestMoney = _bankDao.getEarnestMoney(
                        transTarget.getString("id"), currency);
            }
            if (earnestMoney == null) {
                _bankDao.updateBankBusinessStatus(businessId, "", 3);// 处理失败
                return _bankDao.createFeedback10002(input, "01", "0001", "标的无"
                        + currency + "类型的保证金!");
            }

            // 判断billType的类型（该方法可能在不同的银行有不同的实现，有的银行可能会接受成交款等）
            // 0其它款，1收保证金，2收服务费，3收成交款，4退成交款（委托方退给中心），5暂交款，6错转款
            int billType = _bankDao.getBillType(
                    transLicense.getInt("earnest_money_pay"),
                    transLicense.getInt("status"),
                    transTarget.getDate("end_earnest_time"),
                    transTarget.getDate("begin_limit_time"), inDate);
            // 保存入账单 状态为已审核
            System.out.println("--------transAccountBillApply-->"
                    + transAccountBillApply);
            System.out.println("--------draccbank-->" + draccbank);
            System.out.println("--------draccno-->" + draccno);
            System.out.println("--------draccname-->" + draccname);
            System.out.println("--------amount-->" + amount);
            System.out.println("--------inDate-->" + inDate);
            System.out.println("--------applyId-->" + applyId);
            System.out
                    .println("-------- transTarget.getInt(\"business_type\")-->"
                            + transTarget.getInt("business_type"));
            System.out.println("--------billType-->" + billType);
            System.out.println("--------currency-->" + currency);
            System.out.println("--------busiNo-->" + busiNo);
            billId = _bankDao.saveTransBillAccount(transAccountBillApply,
                    draccbank, draccno, draccname, amount, inDate, applyId,
                    transTarget.getInt("business_type"), billType, 1, currency,
                    busiNo);
            String receiptType = "-1";// 收据类型，如果要保存，会把该值修改成为具体的类型0保证金，1成交款，6错转款，
            if (billType == 1) {// 保证金
                BigDecimal enMoney = _bankDao.getBillSumAmount(applyId,
                        billType);
                if (enMoney.compareTo(earnestMoney) >= 0) {
                    // 如果所有的保证金收据的和 大于了保证金金额
                    _bankDao.updateLicenseEarnestPay(transLicense
                            .getString("id"));
                }
            }
            _bankDao.updateBankBusinessStatus(businessId, billId, 2);// 处理成功
            return _bankDao.createFeedback10002(input, "00", "0000", "来帐通知成功!");
        } else if (transAccountBillApply.getInt("apply_type") == 1) {
            // 关联购物车信息,暂时不做
            _bankDao.updateBankBusinessStatus(businessId, "", 3);// 处理失败
            return _bankDao.createFeedback10002(input, "01", "0001",
                    "未找到有效地竞买申请!");
        } else {
            _bankDao.updateBankBusinessStatus(businessId, "", 3);// 处理失败
            return _bankDao.createFeedback10002(input, "01", "0001",
                    "未找到有效地竞买申请!");
        }
    }

    public ParaMap refundAccount(String accountMessage, String bankid)
            throws Exception {

        XmlVo vo = new XmlVo();
        XmlInput input = new XmlInput();
        XmlOutput output = new XmlOutput();
        XmlUtil xmlUtil = new XmlUtil();

        BankDao _bankDao = new BankDao();
        String busino = "DK" + _bankDao.getBankLSH();
        input.setTrxcode("10006");
        input.setBusino(busino);
        input.setAccmessage(accountMessage);
        input.setRemark("退款");
        input.setBankid(bankid);
        String rsa = input.getTrxcode() + input.getAccmessage()
                + input.getRemark() + input.getBankid();
        rsa = MD5.MD5Encode(rsa);
        input.setRsa(" ");
        output.setStatus("--");
        output.setRetcode("--");
        output.setRetmsg("--");
        output.setAccno("--");
        output.setCommseq("--");
        vo.setInput(input);
        vo.setOutput(output);
        String xml = xmlUtil.writeXMLFile(vo);
        String businessId = _bankDao
                .saveXml(busino, bankid, "10006", xml, null);
        String feedBack = _bankDao.sendXml(xml, bankid, businessId);
        int status = _bankDao.dealBankFeedBack(businessId, feedBack);
        ParaMap out = new ParaMap();
        if (status == 2) {
            out.put("state", 1);
            out.put("businessNo", busino);
            out.put("businessId", businessId);
        } else {
            out.put("state", 0);
        }
        return out;

    }

    public String refundAccountResult(String bankNo, String childNO,
            String bidderNo, String amount, String sequNo) throws Exception {

        XmlVo vo = new XmlVo();
        XmlInput input = new XmlInput();
        XmlOutput output = new XmlOutput();
        XmlUtil xmlUtil = new XmlUtil();

        BankDao _bankDao = new BankDao();
        String busino = "DK" + _bankDao.getBankLSH();
        input.setTrxcode("10007");
        input.setBusino(busino);
        input.setCraccno(childNO);
        input.setDraccno(bidderNo);
        input.setAppbusino(sequNo);
        input.setAmount(this.changeMoneyFormat(new BigDecimal(amount)));
        input.setRemark("查询退款情况");
        input.setBankid(bankNo);
        String rsa = input.getTrxcode() + input.getCraccno()
                + input.getDraccno() + input.getAppbusino() + input.getAmount()
                + input.getRemark();
        rsa = MD5.MD5Encode(rsa);
        input.setRsa(" ");
        output.setStatus("--");
        output.setRetcode("--");
        output.setRetmsg("--");
        output.setAccno("--");
        output.setCommseq("--");
        vo.setInput(input);
        vo.setOutput(output);
        String xml = xmlUtil.writeXMLFile(vo);
        String businessId = _bankDao
                .saveXml(busino, bankNo, "10006", xml, null);
        String feedBack = _bankDao.sendXml(xml, bankNo, businessId);
        int status = _bankDao.dealBankFeedBack(businessId, feedBack);
        String result = null;
        if (status == 2) {
            result = "success";
        } else {
            result = "false";
        }
        return result;
    }

    public String changeMoneyFormat(BigDecimal old) throws Exception {
        DecimalFormat df1 = new DecimalFormat("############0.00");
        String amoutStr = old == null ? "0" : df1.format(old.doubleValue());
        if (amoutStr != null && !"".equals(amoutStr)) {
            if (amoutStr.indexOf(".") > 0) {
                String[] as = amoutStr.split("\\.");
                String secondAS = "00";
                if (as.length > 1) {
                    secondAS = as[1];
                    if (secondAS == null || secondAS.equals("")) {
                        secondAS = "00";
                    } else if (secondAS.length() == 0) {
                        secondAS = "00";
                    } else if (secondAS.length() == 1) {
                        secondAS = secondAS + "0";
                    } else if (secondAS.length() == 2) {
                        secondAS = secondAS;
                    } else if (secondAS.length() > 2) {
                        secondAS = secondAS.substring(0, 2);
                    }
                }
                amoutStr = as[0] + secondAS;
            } else {
                amoutStr = old.toString() + "00";
            }
        }
        return amoutStr;
    }
}
