# Error Summary
I have been trying to extend the micronaut-jpa-hibernate
[tutorial](https://guides.micronaut.io/latest/micronaut-jpa-hibernate-gradle-java.html) to multiple data sources.  I 
have been unable to successfully implements a second datasource/repository. The following error is received when running 
the GenreController unit test.  Any insight on the issue would be much appreciated.  Thanks!

```bash
ERROR i.m.http.server.RouteExecutor - Unexpected error occurred: Failed to inject value for field [entityManager] of 
class: example.micronaut.GenreRepositoryImpl$Intercepted Message: Multiple possible bean candidates found: 
[io.micronaut.configuration.hibernate.jpa.TransactionalSession$Intercepted, io.micronaut.configuration.hibernate.jpa.
TransactionalSession$Intercepted]
```


The second repository I tried to add (`OtherRepositoryImpl`) is implemented in 
java/example/micronaut/OtherBookRepositoryImpl.java. The repository is used in the `GenreController` POST endpoint.  

```bash 
@Singleton
@Repository(value = "other")
abstract class OtherBookRepositoryImpl implements GenericRepository<OtherBook, Long> {

    @Inject
    @PersistenceContext(name = "other")
    private EntityManager entityManager;
    private final ApplicationConfiguration applicationConfiguration;

    public OtherBookRepositoryImpl(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

....

}
```

```bash
@Post
    public HttpResponse<Genre> save(@Body @Valid GenreSaveCommand cmd) {
        Genre genre = genreRepository.save(cmd.getName());

        OtherBook otherBook = otherBookRepository.save("Hello");
        System.out.println("JUST SAVED: " + otherBook.getName());

        return HttpResponse
                .created(genre)
                .headers(headers -> headers.location(location(genre.getId())));
```


I modified the initial repository GenreRepository to be an abstract class that implementing GenericRepository<Genre, Long>

```bash

@Singleton
@Repository(value = "first")
abstract class GenreRepositoryImpl implements GenericRepository<Genre, Long> {
    @Inject
    @PersistenceContext(name = "first")
    private EntityManager entityManager;
    private final ApplicationConfiguration applicationConfiguration;


    private final static List<String> VALID_PROPERTY_NAMES = Arrays.asList("id", "name");

    public GenreRepositoryImpl(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }


  ....
}
````
### Error received when running the GenreController unit test

````bash
ERROR i.m.http.server.RouteExecutor - Unexpected error occurred: Failed to inject value for field [entityManager] of class: example.micronaut.GenreRepositoryImpl$Intercepted

Message: Multiple possible bean candidates found: [io.micronaut.configuration.hibernate.jpa.TransactionalSession$Intercepted, io.micronaut.configuration.hibernate.jpa.TransactionalSession$Intercepted]
Path Taken: new $GenreController$Definition$Intercepted(BeanResolutionContext $beanResolutionContext,BeanContext $beanContext,Qualifier $qualifier,List $interceptors) --> $GenreController$Definition$Intercepted.genreRepository --> GenreRepositoryImpl$Intercepted.entityManager
io.micronaut.context.exceptions.DependencyInjectionException: Failed to inject value for field [entityManager] of class: example.micronaut.GenreRepositoryImpl$Intercepted

Message: Multiple possible bean candidates found: [io.micronaut.configuration.hibernate.jpa.TransactionalSession$Intercepted, io.micronaut.configuration.hibernate.jpa.TransactionalSession$Intercepted]
Path Taken: new $GenreController$Definition$Intercepted(BeanResolutionContext $beanResolutionContext,BeanContext $beanContext,Qualifier $qualifier,List $interceptors) --> $GenreController$Definition$Intercepted.genreRepository --> GenreRepositoryImpl$Intercepted.entityManager
	at io.micronaut.context.AbstractInitializableBeanDefinition.resolveBean(AbstractInitializableBeanDefinition.java:1588)
	at io.micronaut.context.AbstractInitializableBeanDefinition.resolveBean(AbstractInitializableBeanDefinition.java:1549)
	at io.micronaut.context.AbstractInitializableBeanDefinition.getBeanForField(AbstractInitializableBeanDefinition.java:1259)
	at example.micronaut.$GenreRepositoryImpl$Intercepted$Definition.injectBean(Unknown Source)
	at example.micronaut.$GenreRepositoryImpl$Intercepted$Definition.build(Unknown Source)
	at io.micronaut.context.DefaultBeanContext.doCreateBean(DefaultBeanContext.java:2337)
	at io.micronaut.context.DefaultBeanContext.createAndRegisterSingletonInternal(DefaultBeanContext.java:3282)
	at io.micronaut.context.DefaultBeanContext.createAndRegisterSingleton(DefaultBeanContext.java:3268)
	at io.micronaut.context.DefaultBeanContext.getBeanForDefinition(DefaultBeanContext.java:2821)
	at io.micronaut.context.DefaultBeanContext.getBeanInternal(DefaultBeanContext.java:2783)
	at io.micronaut.context.DefaultBeanContext.getBean(DefaultBeanContext.java:1639)
	at io.micronaut.context.AbstractInitializableBeanDefinition.resolveBean(AbstractInitializableBeanDefinition.java:1566)
	at io.micronaut.context.AbstractInitializableBeanDefinition.resolveBean(AbstractInitializableBeanDefinition.java:1549)
	at io.micronaut.context.AbstractInitializableBeanDefinition.getBeanForField(AbstractInitializableBeanDefinition.java:1259)
	at example.micronaut.$GenreController$Definition.injectBean(Unknown Source)
	at example.micronaut.$GenreController$Definition$Intercepted$Definition.build(Unknown Source)
	at io.micronaut.context.DefaultBeanContext.doCreateBean(DefaultBeanContext.java:2337)
	at io.micronaut.context.DefaultBeanContext.createAndRegisterSingletonInternal(DefaultBeanContext.java:3282)
	at io.micronaut.context.DefaultBeanContext.createAndRegisterSingleton(DefaultBeanContext.java:3268)
	at io.micronaut.context.DefaultBeanContext.getBeanForDefinition(DefaultBeanContext.java:2821)
	at io.micronaut.context.DefaultBeanContext.access$100(DefaultBeanContext.java:151)
	at io.micronaut.context.DefaultBeanContext$4.getTarget(DefaultBeanContext.java:541)
	at io.micronaut.context.DefaultBeanContext$4.invoke(DefaultBeanContext.java:584)
	at io.micronaut.web.router.AbstractRouteMatch.execute(AbstractRouteMatch.java:303)
	at io.micronaut.web.router.RouteMatch.execute(RouteMatch.java:111)
	at io.micronaut.http.context.ServerRequestContext.with(ServerRequestContext.java:103)
	at io.micronaut.http.server.RouteExecutor.lambda$executeRoute$14(RouteExecutor.java:636)
	at reactor.core.publisher.FluxDeferContextual.subscribe(FluxDeferContextual.java:49)
	at reactor.core.publisher.InternalFluxOperator.subscribe(InternalFluxOperator.java:62)
	at reactor.core.publisher.FluxSubscribeOn$SubscribeOnSubscriber.run(FluxSubscribeOn.java:194)
	at io.micronaut.reactive.reactor.instrument.ReactorInstrumentation.lambda$null$0(ReactorInstrumentation.java:62)
	at reactor.core.scheduler.WorkerTask.call(WorkerTask.java:84)
	at reactor.core.scheduler.WorkerTask.call(WorkerTask.java:37)
	at io.micronaut.scheduling.instrument.InvocationInstrumenterWrappedCallable.call(InvocationInstrumenterWrappedCallable.java:53)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
	at java.base/java.lang.Thread.run(Thread.java:829)
Caused by: io.micronaut.context.exceptions.NonUniqueBeanException: Multiple possible bean candidates found: [io.micronaut.configuration.hibernate.jpa.TransactionalSession$Intercepted, io.micronaut.configuration.hibernate.jpa.TransactionalSession$Intercepted]
	at io.micronaut.context.DefaultBeanContext.findConcreteCandidate(DefaultBeanContext.java:2430)
	at io.micronaut.context.DefaultApplicationContext.findConcreteCandidate(DefaultApplicationContext.java:454)
	at io.micronaut.context.DefaultBeanContext.lastChanceResolve(DefaultBeanContext.java:3254)
	at io.micronaut.context.DefaultBeanContext.findConcreteCandidateNoCache(DefaultBeanContext.java:3141)
	at io.micronaut.context.DefaultBeanContext.findConcreteCandidate(DefaultBeanContext.java:3055)
	at io.micronaut.context.DefaultBeanContext.getBeanInternal(DefaultBeanContext.java:2751)
	at io.micronaut.context.DefaultBeanContext.getBean(DefaultBeanContext.java:1639)
	at io.micronaut.context.AbstractInitializableBeanDefinition.resolveBean(AbstractInitializableBeanDefinition.java:1566)
	... 37 common frames omitted


```



## Micronaut 3.1.3 Documentation

- [User Guide](https://docs.micronaut.io/3.1.3/guide/index.html)
- [API Reference](https://docs.micronaut.io/3.1.3/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/3.1.3/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---

## Feature http-client documentation

- [Micronaut HTTP Client documentation](https://docs.micronaut.io/latest/guide/index.html#httpClient)
