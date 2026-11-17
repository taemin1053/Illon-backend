package com.career.analytics.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QScrapedJob is a Querydsl query type for ScrapedJob
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QScrapedJob extends EntityPathBase<ScrapedJob> {

    private static final long serialVersionUID = -595107539L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QScrapedJob scrapedJob = new QScrapedJob("scrapedJob");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DatePath<java.time.LocalDate> deadline = createDate("deadline", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> jobCreatedAt = createDateTime("jobCreatedAt", java.time.LocalDateTime.class);

    public final StringPath jobId = createString("jobId");

    public final QUsers user;

    public QScrapedJob(String variable) {
        this(ScrapedJob.class, forVariable(variable), INITS);
    }

    public QScrapedJob(Path<? extends ScrapedJob> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QScrapedJob(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QScrapedJob(PathMetadata metadata, PathInits inits) {
        this(ScrapedJob.class, metadata, inits);
    }

    public QScrapedJob(Class<? extends ScrapedJob> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUsers(forProperty("user"), inits.get("user")) : null;
    }

}

