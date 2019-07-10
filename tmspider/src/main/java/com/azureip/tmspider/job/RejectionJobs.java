package com.azureip.tmspider.job;

import com.azureip.tmspider.service.RegistrationService;
import com.azureip.tmspider.service.RejectionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 驳回数据相关定时任务
 */
@Component
public class RejectionJobs {
    private static final Logger LOG = LogManager.getLogger(RejectionJobs.class);
    private final RejectionService rejectionService;
    private final RegistrationService registrationService;

    @Autowired
    public RejectionJobs(RejectionService rejectionService, RegistrationService registrationService) {
        this.rejectionService = rejectionService;
        this.registrationService = registrationService;
    }

    // 商标状态链接添加任务（每天凌晨0点15分执行）
    @Scheduled(cron = "0 0 0 * * ?")
    public synchronized void registrationQueryJob() {
        LOG.info("====> 商标状态链接添加任务开始 <====");
        File srcDir = new File("D:/TMSpider/link_src");
        File tarDir = new File("D:/TMSpider/link_tar");
        try {
            List<String> fileNames = registrationService.optRejections(srcDir, tarDir);
            if (CollectionUtils.isEmpty(fileNames)) {
                LOG.warn("文件夹“link_src”为空");
            } else {
                StringBuilder files = new StringBuilder();
                for (String fileName : fileNames) {
                    files.append(fileName);
                }
                LOG.info("已添加链接的文件有：" + files.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOG.info("====> 商标状态链接添加任务结束 <====");
    }

    // 驳回信息查询任务（每天凌晨0点30分执行）
    @Scheduled(cron = "0 30 0 * * ?")
    public synchronized void rejectionQueryJob() {
        LOG.info("====> 驳回信息查询任务开始 <====");
        File srcDir = new File("D:/TMSpider/rej_src");
        File tarDir = new File("D:/TMSpider/rej_tar");
        try {
            List<String> fileNames = rejectionService.queryRejections(srcDir, tarDir);
            if (CollectionUtils.isEmpty(fileNames)) {
                LOG.warn("文件夹“rej_src”为空");
            } else {
                StringBuilder files = new StringBuilder();
                for (String fileName : fileNames) {
                    files.append(fileName);
                }
                LOG.info("已查询驳回的文件有：" + files.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOG.info("====> 驳回信息查询任务结束 <====");
    }

    /*  【Cron表达式说明】
    第一位：表示秒，取值0-59
    第二位：表示分，取值0-59
    第三位：表示小时，取值0-23
    第四位：日期天/日，取值1-31
    第五位：日期月份，取值1-12
    第六位：星期，取值1-7，星期一，星期二……另外：1表示星期天，2表示星期一；
    第七位：年份，可以留空（在Spring定时任务中无效），取值1970-2099。
        【符号说明】
    * ：可以理解为每的意思，每秒，每分，每天，每月，每年...
    ? ：问号只能出现在日期和星期这两个位置，表示这个位置的值不确定，每天3点执行，所以第六位星期的位置，我们是不需要关注的，就是不确定的值。
        同时：日期和星期是两个相互排斥的元素，通过问号来表明不指定值。比如，1月10日，比如是星期1，如果在星期的位置是另指定星期二，就前后冲突矛盾了。
    - ：表达一个范围，如在小时字段中使用“10-12”，则表示从10到12点，即10,11,12
    , ：表达一个列表值，如在星期字段中使用“1,2,4”，则表示星期一，星期二，星期四
    / ：如：x/y，x是开始值，y是步长，比如在第一位（秒） 0/15就是，从0秒开始，每15秒，最后就是0，15，30，45，60（另：* /y，等同于0/y）。
    */
}
