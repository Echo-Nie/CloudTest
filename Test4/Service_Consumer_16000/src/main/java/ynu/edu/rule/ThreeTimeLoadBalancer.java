package ynu.edu.rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreeTimeLoadBalancer implements ReactorServiceInstanceLoadBalancer {
    private static final Logger log = LoggerFactory.getLogger(ThreeTimeLoadBalancer.class);

    private AtomicInteger callCount = new AtomicInteger(0); // 调用计数器，使用AtomicInteger确保线程安全
    private AtomicInteger instanceIndex = new AtomicInteger(0); // 当前服务实例索引
    private final String serviceId;
    private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSuppliers;

    public ThreeTimeLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSuppliers,
            String serviceId) {
        this.serviceInstanceListSuppliers = serviceInstanceListSuppliers;
        this.serviceId = serviceId;
        log.info("ThreeTimeLoadBalancer created for service: {}", serviceId);
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = this.serviceInstanceListSuppliers.getIfAvailable();
        return supplier.get().next().map(this::getInstanceResponse);
    }

    public Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances) {
        if (instances.isEmpty()) {
            log.warn("No instances available for service: {}", serviceId);
            return new EmptyResponse();
        }

        int size = instances.size();

        // 取模运算确保索引在合法范围内
        int currentIndex = instanceIndex.get() % size;

        // 获取当前实例
        ServiceInstance serviceInstance = instances.get(currentIndex);

        // 更新调用计数
        int currentCallCount = callCount.incrementAndGet();

        log.info("Selected instance: {} (index: {}, call count: {}/3)",
                serviceInstance.getUri(), currentIndex, currentCallCount);

        // 如果已经调用了3次，重置计数并更新实例索引
        if (currentCallCount >= 3) {
            callCount.set(0);
            instanceIndex.incrementAndGet();
            log.info("Switching to next instance, new index: {}", (instanceIndex.get() % size));
        }

        return new DefaultResponse(serviceInstance);
    }
}
