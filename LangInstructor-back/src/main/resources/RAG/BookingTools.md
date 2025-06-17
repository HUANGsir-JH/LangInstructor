# BookingTools.java 示例

**示例目的:**
此文件定义了客户支持代理可以使用的预订相关工具。这些工具通过 `@Tool` 注解暴露给 LLM，允许 LLM 在处理用户请求时调用这些方法来获取预订详情或取消预订。

**关键类和方法:**
- `@Component`: Spring 框架注解，表示这是一个 Spring 管理的组件。
- `@Autowired`: Spring 框架注解，用于自动注入 `BookingService` 实例。
- `@Tool`: LangChain4j 注解，将方法标记为可供 LLM 调用的工具。
- `getBookingDetails(String bookingNumber, String customerName, String customerSurname)`: 获取指定预订号和客户姓名的预订详情。
- `cancelBooking(String bookingNumber, String customerName, String customerSurname)`: 取消指定预订号和客户姓名的预订。
- `BookingService`: 负责处理实际预订逻辑的服务类（在此文件中未定义，但被 `BookingTools` 依赖）。

**注意事项:**
- 这是一个 Spring 组件，因此需要 Spring Boot 环境才能正常运行。
- `@Tool` 注解的方法会自动被 LangChain4j 识别并转换为 LLM 可以理解的工具描述。
- `System.out.println` 语句用于在控制台输出工具调用的日志，便于调试和跟踪。

```java
package dev.langchain4j.example;

import dev.langchain4j.example.booking.Booking;
import dev.langchain4j.example.booking.BookingService;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookingTools {

    @Autowired
    private BookingService bookingService;

    @Tool
    public Booking getBookingDetails(String bookingNumber, String customerName, String customerSurname) {
        System.out.println("==========================================================================================");
        System.out.printf("[Tool]: Getting details for booking %s for %s %s...%n", bookingNumber, customerName, customerSurname);
        System.out.println("==========================================================================================");

        return bookingService.getBookingDetails(bookingNumber, customerName, customerSurname);
    }

    @Tool
    public void cancelBooking(String bookingNumber, String customerName, String customerSurname) {
        System.out.println("==========================================================================================");
        System.out.printf("[Tool]: Cancelling booking %s for %s %s...%n", bookingNumber, customerName, customerSurname);
        System.out.println("==========================================================================================");

        bookingService.cancelBooking(bookingNumber, customerName, customerSurname);
    }
}
