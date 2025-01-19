package com.github.liushidai.img_server.util;

/**
 * 雪花算法ID生成工具类，用于生成分布式系统中的唯一ID
 */
public class SnowflakeIdUtil {

    // 静态的雪花算法ID生成器实例，默认为 workerId 和 datacenterId 都为 0
    private static SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(0);

    // 当前使用的 workerId，使用 volatile 保证可见性
    /** 使用3bit存储，移除数据中心id，所以最大7
     * 所以生成的最大值为2的49次方减一
     * 562949953421311
     */
    private static volatile long currentWorkerId;

    /**
     * 初始化 SnowflakeIdGenerator，更新 workerId
     *
     * @param workerId 工作机器ID (0~7)
     */
    public static void initialize(long workerId) {
        System.out.println("Initializing SnowflakeIdGenerator, workerId: " + workerId);
        idGenerator = new SnowflakeIdGenerator(workerId);
        currentWorkerId = workerId;
    }

    /**
     * 使用该方法获取下一个唯一ID
     *
     * @return 生成的唯一ID
     */
    public static long generateNextId() {
        return idGenerator.generateNextId();
    }


    /**
     * 内部类，实现雪花算法的具体逻辑
     */
    private static class SnowflakeIdGenerator {

        // 开始时间截 (2015-01-01)
        private final long epoch = 1420041600000L;

        // 机器id所占的位数
        private final long workerIdBitCount = 3L;

        // 序列在id中占的位数
        private final long sequenceBitCount = 5L;

        // 支持的最大机器id，结果是7 (这个移位算法可以很快地计算出几位二进制数所能表示的最大十进制数)
        private final long maxWorkerId = ~(-1L << workerIdBitCount);

        // 机器ID向左移5位
        private final long workerIdShift = sequenceBitCount;

        // 时间截向左移8位(3+5)
        private final long timestampShift = sequenceBitCount + workerIdBitCount;

        // 生成序列的掩码，这里为31 (0b11111=0x1f=31)
        private final long sequenceMask = ~(-1L << sequenceBitCount);

        // 工作机器ID(0~7)
        private final long workerId;

        // 毫秒内序列(0~31)
        private long sequence = 0L;

        // 上次生成ID的时间截
        private long lastTimestamp = -1L;


        /**
         * 构造函数
         *
         * @param workerId 工作ID (0~7)
         */
        public SnowflakeIdGenerator(long workerId) {
            validateId(workerId, maxWorkerId, "Worker Id");
            this.workerId = workerId;
        }

        /**
         * 验证输入的ID是否在有效范围内
         *
         * @param id         要验证的ID
         * @param maxId      最大允许的ID
         * @param idTypeName ID的类型名称
         */
        private void validateId(long id, long maxId, String idTypeName) {
            if (id > maxId || id < 0) {
                throw new IllegalArgumentException(String.format("%s can't be greater than %d or less than 0", idTypeName, maxId));
            }
        }

        /**
         * 获得下一个ID (该方法是线程安全的)
         *
         * @return 生成的下一个唯一ID
         */
        public synchronized long generateNextId() {
            long timestamp = getCurrentTime();

            // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
            if (timestamp < lastTimestamp) {
                handleClockBackwards(lastTimestamp, timestamp);
            }

            // 如果是同一时间生成的，则进行毫秒内序列处理
            if (lastTimestamp == timestamp) {
                sequence = (sequence + 1) & sequenceMask;
                // 毫秒内序列溢出，阻塞到下一个毫秒，获得新的时间戳
                if (sequence == 0) {
                    timestamp = waitUntilNextMillis(lastTimestamp);
                }
            } else {
                // 时间戳改变，毫秒内序列重置
                sequence = 0L;
            }

            // 上次生成ID的时间截更新
            lastTimestamp = timestamp;

            // 移位并通过或运算拼到一起组成64位的ID
            return ((timestamp - epoch) << timestampShift)
                    | (workerId << workerIdShift)
                    | sequence;
        }

        /**
         * 处理时钟回退的情况
         *
         * @param lastTimestamp 上次生成ID的时间戳
         * @param timestamp     当前时间戳
         */
        private void handleClockBackwards(long lastTimestamp, long timestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        /**
         * 阻塞到下一个毫秒，直到获得新的时间戳
         *
         * @param lastTimestamp 上次生成ID的时间截
         * @return 当前时间戳
         */
        private long waitUntilNextMillis(long lastTimestamp) {
            long timestamp = getCurrentTime();
            while (timestamp <= lastTimestamp) {
                timestamp = getCurrentTime();
            }
            return timestamp;
        }

        /**
         * 返回以毫秒为单位的当前时间
         *
         * @return 当前时间(毫秒)
         */
        private long getCurrentTime() {
            return System.currentTimeMillis();
        }
    }
}