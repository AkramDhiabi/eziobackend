package com.gemalto.eziomobile.demo.controller.usermaster;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.exception.ControllerException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserMasterOperationCrontoller {

    private static final LoggerUtil logger = new LoggerUtil(UserMasterController.class.getClass());

    @Autowired
    private UsermasterService userMasterService;

    static class RecoverForm
    {
        public RecoverForm()
        {}
        private String emailAddress;
        private String recoverToken;
        private String password;

        public String getEmailAddress() {
            return emailAddress;
        }

        public String getRecoverToken() {
            return recoverToken;
        }

        public String getPassword() {
            return password;
        }
    }

    /**
     * @param payload
     * @return
     * @throws ControllerException
     */
    @RequestMapping(value = "/recoveraccount.user.action", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public ResultStatus recoverAccount(@RequestBody RecoverForm payload)
            throws ControllerException {
        ResultStatus resultStatus = new ResultStatus();
        boolean flag = false;
        boolean changepwd = false;
        String emailAddress = payload.getEmailAddress();
        String recoverToken = payload.getRecoverToken();
        String password = payload.getPassword();
        logger.info("Init recover");
        try {
            if (emailAddress != null && !emailAddress.equals("") && recoverToken != null && !recoverToken.equals("")) {

                //check if key is valid,
                //Key is valid for 3 days only
                flag = userMasterService.isRecoverTokenValid(emailAddress, recoverToken);
                logger.info("Is Recover Token Valid : " + flag);

                if (flag) {
                    changepwd = userMasterService.updatePasswordByEmailAndRecoverToken(emailAddress, recoverToken, password);
                    logger.info("Print the value of changepwd :" + changepwd);
                    if (changepwd) {
                        userMasterService.updateRecoverTokenByEmailAndRecoverToken(emailAddress, recoverToken);
                        resultStatus.setMessage("Password updated successfully");
                        resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
                        resultStatus.setStatusCode(HttpStatus.OK);
                    } else {
                        resultStatus.setMessage("Password updation failed");
                        resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
                        resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
                    }

                } else {
                    resultStatus.setMessage("Password updation failed");
                    resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
                    resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
                }
            } else {
                resultStatus.setMessage("Password updation failed");
                resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
                resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("");
            throw new ControllerException(e);
        }
        return resultStatus;
    }
}
