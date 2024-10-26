package com.gl.eirs.iplocationprocessor.service;


import com.gl.eirs.iplocationprocessor.dto.FileDto;
import com.gl.eirs.iplocationprocessor.entity.app.Ipv4;
import com.gl.eirs.iplocationprocessor.repository.app.Ipv4Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;

@Service
public class FileServiceIpV4 implements IFileService {

    @Value("${file.separator.parameter}")
    private String fileSeparator;

    @Value("${error.file.path}")
    String errorFilePath;

    @Autowired
    Ipv4Repository ipv4Repository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public boolean processAddFile(FileDto fileDto, String errorFileName) {
        // read file and process the entries
        int failureCount = 0;
        int succesCount = 0;
        int emptyRecords = 0;
        logger.info("Full Path with File Name : {} ", fileDto.getFilePath() + "  /  " + fileDto.getFileName());
        try (BufferedReader reader = new BufferedReader(new FileReader(fileDto.getFilePath() + "/" + fileDto.getFileName()))) {
            File outFile = new File(errorFilePath.replace("<ipType>", "ipv4") + "/" + errorFileName);
            PrintWriter writer = new PrintWriter(outFile);
            try {
                String record;
                while ((record = reader.readLine()) != null) {
                    if (record.isEmpty()) {
                        emptyRecords++;
                        continue;
                    }

                    String[] ipRecord = record.split(fileSeparator, -1);
                    if (ipRecord.length != 4) {
                        logger.error("The record length is less than 4 {}", Arrays.stream(ipRecord).toList());
                        writer.println(record);
                        continue;
                    }
                    for (int i = 0; i < ipRecord.length; i++) {
                        ipRecord[i] = ipRecord[i].replaceAll("\"", "");
                    }
                    Ipv4 ip4 = new Ipv4(ipRecord);
                    try {
                        logger.info("Inserting the entry {}", ip4);
                        ipv4Repository.save(ip4);
                        succesCount++;
                    } catch (Exception ex) {
                        logger.error("The entry failed to save in Ipv4 table, {}", ip4);
                        failureCount++;
                    }
                }
                writer.close();
            } catch (Exception ex) {
                logger.error("File processing for file {}, failed due to {}", fileDto.getFileName(), ex.getMessage());
                fileDto.setFailedRecords(failureCount);
                fileDto.setSuccessRecords(succesCount);
                fileDto.setTotalRecords(fileDto.getTotalRecords() - emptyRecords);
                return true;
            }

        } catch (FileNotFoundException ex) {
            logger.error("File processing for file {} . failed due to {}", fileDto.getFileName(), ex.getMessage());
            fileDto.setFailedRecords(failureCount);
            fileDto.setSuccessRecords(succesCount);
            fileDto.setTotalRecords(fileDto.getTotalRecords() - emptyRecords);
            return true;
        } catch (IOException ex) {
            logger.error("File processing for file {}, failed due to {}", fileDto.getFileName(), ex.getMessage());
            fileDto.setFailedRecords(failureCount);
            fileDto.setSuccessRecords(succesCount);
            fileDto.setTotalRecords(fileDto.getTotalRecords() - emptyRecords);
            return true;
        } catch (Exception ex) {
            logger.error("File processing for file {}, failed due to {}", fileDto.getFileName(), ex.getMessage());
            fileDto.setFailedRecords(failureCount);
            fileDto.setSuccessRecords(succesCount);
            fileDto.setTotalRecords(fileDto.getTotalRecords() - emptyRecords);
            return true;
        }

        fileDto.setFailedRecords(failureCount);
        fileDto.setSuccessRecords(succesCount);
        fileDto.setTotalRecords(fileDto.getTotalRecords() - emptyRecords);
        return false;
    }

    public boolean processDelFile(FileDto fileDto, String errorFileName) {
        int failureCount = 0;
        int succesCount = 0;
        int emptyRecords = 0;
        logger.debug("Full file Path : " +  fileDto.getFilePath() + "/" + fileDto.getFileName() )  ;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileDto.getFilePath() + "/" + fileDto.getFileName()))) {
            File outFile = new File(errorFilePath.replace("<ipType>", "ipv4") + "/" + errorFileName);
            PrintWriter writer = new PrintWriter(outFile);
            try {
                String record;
                while ((record = reader.readLine()) != null) {
                    if (record.isEmpty()) {
                        emptyRecords++;
                        continue;
                    }

                    String[] ipRecord = record.split(fileSeparator, -1);
                    if (ipRecord.length != 4) {
                        logger.error("The record length is less than 4 {}", Arrays.stream(ipRecord).toList());
                        writer.println(record);
                        continue;
                    }
                    for (int i = 0; i < ipRecord.length; i++) {
                        ipRecord[i] = ipRecord[i].replaceAll("\"", "");
                    }
                    Ipv4 ip4 = new Ipv4(ipRecord);
                    try {
                        logger.info("Deleting the entry {}", ip4);
                        ipv4Repository.deleteByData(ip4.getStartIpNumber(), ip4.getEndIpNumber(), ip4.getCountryCode(), ip4.getCountryName());
//                        ipv4Repository.delete(ip4);
                        succesCount++;
                    } catch (Exception ex) {
                        logger.error("The entry failed to delete in Ipv4 table, {}", ip4);
                        failureCount++;
                    }
                }
                writer.close();
            } catch (Exception ex) {
                logger.error("File processing for file {}, failed due to {}", fileDto.getFileName(), ex.getMessage());
                fileDto.setFailedRecords(failureCount);
                fileDto.setSuccessRecords(succesCount);
                fileDto.setTotalRecords(fileDto.getTotalRecords() - emptyRecords);
                return true;
            }

        } catch (FileNotFoundException ex) {
            logger.error("File processing for file {} . Failed due to {}", fileDto.getFileName(), ex.getMessage());
            fileDto.setFailedRecords(failureCount);
            fileDto.setSuccessRecords(succesCount);
            fileDto.setTotalRecords(fileDto.getTotalRecords() - emptyRecords);
            return true;
        } catch (IOException ex) {
            logger.error("File processing for file {}, failed due to {}", fileDto.getFileName(), ex.getMessage());
            fileDto.setFailedRecords(failureCount);
            fileDto.setSuccessRecords(succesCount);
            fileDto.setTotalRecords(fileDto.getTotalRecords() - emptyRecords);
            return true;
        } catch (Exception ex) {
            logger.error("File processing for file {}, failed due to {}", fileDto.getFileName(), ex.getMessage());
            fileDto.setFailedRecords(failureCount);
            fileDto.setSuccessRecords(succesCount);
            fileDto.setTotalRecords(fileDto.getTotalRecords() - emptyRecords);
            return true;
        }
        fileDto.setFailedRecords(failureCount);
        fileDto.setSuccessRecords(succesCount);
        fileDto.setTotalRecords(fileDto.getTotalRecords() - emptyRecords);
        return false;
    }
}
