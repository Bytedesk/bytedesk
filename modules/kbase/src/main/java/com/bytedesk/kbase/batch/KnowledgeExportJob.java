/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-21 15:53:49
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-21 16:58:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.batch;

// import java.util.HashMap;
// import java.util.Map;

// import org.springframework.batch.core.Job;
// import org.springframework.batch.core.JobExecution;
// import org.springframework.batch.core.JobParameter;
// import org.springframework.batch.core.JobParameters;
// import org.springframework.batch.core.Step;
// import org.springframework.batch.core.job.builder.JobBuilder;
// import org.springframework.batch.core.launch.JobLauncher;
// import org.springframework.batch.core.launch.support.RunIdIncrementer;
// import org.springframework.batch.core.repository.JobRepository;
// import org.springframework.batch.core.step.builder.StepBuilder;
// import org.springframework.batch.item.file.FlatFileItemWriter;
// import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
// import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
// import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
// import org.springframework.batch.repeat.RepeatStatus;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.io.FileSystemResource;
// // import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.transaction.PlatformTransactionManager;

// import com.bytedesk.kbase.article.ArticleRepository;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// /**
//  * 知识库导出批处理作业
//  */
// @Slf4j
// @Configuration
// @RequiredArgsConstructor
// public class KnowledgeExportJob {

//     private final JobRepository jobRepository;
//     private final PlatformTransactionManager transactionManager;
//     // private final JobLauncher jobLauncher;
//     private final ArticleRepository articleRepository;

//     @Bean
//     public Job kbaseKnowledgeExportJob() {
//         return new JobBuilder("knowledgeExportJob", jobRepository)
//                 .start(prepareExportStep())
//                 .next(exportKnowledgeStep())
//                 .build();
//     }

//     @Bean
//     public Step prepareExportStep() {
//         return new StepBuilder("prepareExportStep", jobRepository)
//                 .tasklet((contribution, chunkContext) -> {
//                     log.info("准备知识库导出");
//                     return RepeatStatus.FINISHED;
//                 }, transactionManager)
//                 .build();
//     }

//     @Bean
//     public Step exportKnowledgeStep() {
//         return new StepBuilder("exportKnowledgeStep", jobRepository)
//                 .tasklet((contribution, chunkContext) -> {
//                     String format = chunkContext.getStepContext()
//                             .getJobParameters()
//                             .getOrDefault("format", "csv").toString();
                    
//                     log.info("执行知识库导出步骤，格式: {}", format);
//                     return RepeatStatus.FINISHED;
//                 }, transactionManager)
//                 .build();
//     }

//     /**
//      * 定义导出步骤
//      * @return Step 导出步骤
//      */
//     @Bean
//     public Step exportStep() {
//         return new StepBuilder("exportStep", jobRepository)
//                 .tasklet((contribution, chunkContext) -> {
//                     log.info("开始执行知识库导出作业...");
//                     // 实际导出逻辑可在此实现
//                     // 这里只是简单示例，实际应使用ItemReader和ItemWriter
//                     long count = articleRepository.count();
//                     log.info("知识库文章总数: {}", count);
//                     log.info("知识库导出作业完成");
//                     return RepeatStatus.FINISHED;
//                 }, transactionManager)
//                 .build();
//     }

//     /**
//      * 定时触发知识库导出作业
//      * 每天凌晨2点执行
//      */
//     // @Scheduled(cron = "0 0 2 * * ?")
//     // public void scheduleKnowledgeExport() {
//     //     try {
//     //         Map<String, JobParameter<?>> confMap = new HashMap<>();
//     //         confMap.put("time", new JobParameter<>(System.currentTimeMillis(), Long.class));
//     //         confMap.put("outputPath", new JobParameter<>("exports/knowledge_" + System.currentTimeMillis() + ".csv", String.class));
            
//     //         JobParameters jobParameters = new JobParameters(confMap);
            
//     //         JobExecution jobExecution = jobLauncher.run(knowledgeExportJob(), jobParameters);
//     //         log.info("知识库导出作业完成，状态: {}", jobExecution.getStatus());
//     //     } catch (Exception e) {
//     //         log.error("知识库导出作业执行失败", e);
//     //     }
//     // }

//     /**
//      * CSV文件写入器示例（实际使用时需配合ItemReader使用）
//      * 
//      * @return FlatFileItemWriter CSV文件写入器
//      */
//     // @Bean
//     public FlatFileItemWriter<KnowledgeExportItem> writer() {
//         return new FlatFileItemWriterBuilder<KnowledgeExportItem>()
//                 .name("knowledgeItemWriter")
//                 .resource(new FileSystemResource("exports/knowledge.csv"))
//                 .lineAggregator(new DelimitedLineAggregator<KnowledgeExportItem>() {
//                     {
//                         setDelimiter(",");
//                         setFieldExtractor(new BeanWrapperFieldExtractor<KnowledgeExportItem>() {
//                             {
//                                 setNames(new String[] { "id", "title", "content", "category", "createdAt" });
//                             }
//                         });
//                     }
//                 })
//                 .headerCallback(writer -> writer.write("ID,标题,内容,分类,创建时间"))
//                 .build();
//     }

//     /**
//      * 知识库导出项目
//      */
//     public static class KnowledgeExportItem {
//         private String id;
//         private String title;
//         private String content;
//         private String category;
//         private String createdAt;
        
//         // Getters and setters
//         public String getId() { return id; }
//         public void setId(String id) { this.id = id; }
        
//         public String getTitle() { return title; }
//         public void setTitle(String title) { this.title = title; }
        
//         public String getContent() { return content; }
//         public void setContent(String content) { this.content = content; }
        
//         public String getCategory() { return category; }
//         public void setCategory(String category) { this.category = category; }
        
//         public String getCreatedAt() { return createdAt; }
//         public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
//     }
// } 