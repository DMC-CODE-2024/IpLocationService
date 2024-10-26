package com.gl.eirs.iplocationprocessor.service;


import com.gl.eirs.iplocationprocessor.builder.ModulesAuditTrailBuilder;
import com.gl.eirs.iplocationprocessor.dto.FileDto;
import com.gl.eirs.iplocationprocessor.entity.aud.ModulesAuditTrail;
import com.gl.eirs.iplocationprocessor.repository.aud.ModulesAuditTrailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import static com.gl.eirs.iplocationprocessor.constants.Constants.*;

@Component
public class MainService {

    @Value("${delta.file.path}")
    String filePath;

    @Autowired
    ModulesAuditTrailRepository modulesAuditTrailRepository;

    @Autowired
    FileServiceIpV4 fileServiceIpV4;

    @Autowired
    FileServiceIpV6 fileServiceIpV6;

    @Autowired
    ModulesAuditTrailBuilder modulesAuditTrailBuilder;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void processDeltaFiles(String ipType) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String addFileName = "ip_location_country_add_" + ipType + "_diff_" + sdf.format(date).trim() + ".csv";
        String delFileName = "ip_location_country_del_" + ipType + "_diff_" + sdf.format(date).trim() + ".csv";
        String addErrorFileName = "ip_location_country_add_" + ipType + "_error_" + sdf.format(date).trim() + ".csv";
        String delErrorFilename = "ip_location_country_del_" + ipType + "_error_" + sdf.format(date).trim() + ".csv";

        logger.debug("File path is ::  {} ", filePath.replace("<ipType>", ipType));
        FileDto addFileDto = new FileDto(addFileName, filePath.replace("<ipType>", ipType));
        FileDto delFileDto = new FileDto(delFileName, filePath.replace("<ipType>", ipType));

        int moduleAuditId;
        long startTime = System.currentTimeMillis();
        ModulesAuditTrail modulesAuditTrail;
        modulesAuditTrail = modulesAuditTrailBuilder.forInsert(201, "INITIAL", "NA", moduleName, featureName + ipType, "", "", LocalDateTime.now());
        ModulesAuditTrail entity = modulesAuditTrailRepository.save(modulesAuditTrail);
        moduleAuditId = entity.getId();
        if (ipType.equalsIgnoreCase("ipv4")) {
            try {
                fileServiceIpV4.processDelFile(delFileDto, delErrorFilename);
                fileServiceIpV4.processAddFile(addFileDto, addErrorFileName);
            } catch (Exception ex) {
                logger.error("The file processing failed for ipv4 diff file");
                logger.info("Summary for add file {} is {}", addFileDto.getFileName(), addFileDto);
                logger.info("Summary for del file {} is {}", delFileDto.getFileName(), delFileDto);
                modulesAuditTrailRepository.updateModulesAudit(501, "FAIL", "The file processing failed for ipv4 diff file",
                        (int) (addFileDto.getTotalRecords() + delFileDto.getTotalRecords()),
                        (int) (addFileDto.getFailedRecords() + delFileDto.getFailedRecords()),
                        (int) (System.currentTimeMillis() - startTime), LocalDateTime.now(), (int) (addFileDto.getSuccessRecords() + delFileDto.getSuccessRecords()),
                        moduleAuditId);
                System.exit(1);
            }

        } else if (ipType.equalsIgnoreCase("ipv6")) {
            try {
                boolean delFileResponse = fileServiceIpV6.processDelFile(delFileDto, delErrorFilename);
                boolean addFileResponse = fileServiceIpV6.processAddFile(addFileDto, addErrorFileName);
            } catch (Exception ex) {
                logger.error("The file processing failed for ipv6 diff file");
                logger.info("Summary for add file {} is {}", addFileDto.getFileName(), addFileDto);
                logger.info("Summary for del file {} is {}", delFileDto.getFileName(), delFileDto);
                modulesAuditTrailRepository.updateModulesAudit(501, "FAIL", "The file processing failed for ipv6 diff file",
                        (int) (addFileDto.getTotalRecords() + delFileDto.getTotalRecords()),
                        (int) (addFileDto.getFailedRecords() + delFileDto.getFailedRecords()),
                        (int) (System.currentTimeMillis() - startTime), LocalDateTime.now(), (int) (addFileDto.getSuccessRecords() + delFileDto.getSuccessRecords()),
                        moduleAuditId);
                System.exit(1);
            }
        }

        logger.info("Summary for add file {} is {}", addFileDto.getFileName(), addFileDto);
        logger.info("Summary for del file {} is {}", delFileDto.getFileName(), delFileDto);

        modulesAuditTrailRepository.updateModulesAudit(200, "SUCCESS", "NA",
                (int) (addFileDto.getTotalRecords() + delFileDto.getTotalRecords()),
                (int) (addFileDto.getFailedRecords() + delFileDto.getFailedRecords()),
                (int) (System.currentTimeMillis() - startTime), LocalDateTime.now(), (int) (addFileDto.getSuccessRecords() + delFileDto.getSuccessRecords()),
                moduleAuditId);

    }

}
