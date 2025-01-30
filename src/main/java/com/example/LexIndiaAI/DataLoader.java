package com.example.LexIndiaAI;



import jakarta.annotation.PostConstruct;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {
    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private JdbcClient jdbcClient;

    @Value("classpath:/India_Constitution.pdf")
    private Resource pdfResouce;


    /*
        This method will be instanciated only when the bean is created or called
     */
    @PostConstruct
    public void init(){
        Integer count = jdbcClient.sql("select COUNT(*) from vector_store")
                .query(Integer.class)
                .single();
        System.out.println("No of records found in database : "+count);
        if(count == 0){
            System.out.println("Loading India Constitution.pdf in to database");

            PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                    .withPagesPerDocument(1)
                    .build();
            PagePdfDocumentReader reader = new PagePdfDocumentReader(pdfResouce, config);

            var textSplitter = new TokenTextSplitter();
            vectorStore.accept(textSplitter.apply(reader.get()));
            System.out.println("Application is ready to Serve the Requests");


        }
    }

}
