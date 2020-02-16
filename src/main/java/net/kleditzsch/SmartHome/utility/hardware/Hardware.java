package net.kleditzsch.SmartHome.utility.hardware;

import net.kleditzsch.SmartHome.utility.file.FileUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Hardware {

    /**
     * CPU Kern Info
     */
    public static class CoreInfo {

        private int id = 0;
        private double minClock = 0;
        private double currentClock = 0;
        private double maxClock = 0;

        public CoreInfo() {}

        public CoreInfo(int id, double minClock, double currentClock, double maxClock) {
            this.id = id;
            this.minClock = minClock;
            this.currentClock = currentClock;
            this.maxClock = maxClock;
        }

        public int getId() {
            return id;
        }

        public double getMinClock() {
            return minClock;
        }

        public double getCurrentClock() {
            return currentClock;
        }

        public double getMaxClock() {
            return maxClock;
        }
    }

    /**
     * CPU Info
     */
    public static class CpuInfo {

        private int coreCount;
        private List<CoreInfo> cores;
        private String vendor;
        private String modelName;

        public CpuInfo(int coreCount, List<CoreInfo> cores, String vendor, String modelName) {
            this.coreCount = coreCount;
            this.cores = cores;
            this.vendor = vendor;
            this.modelName = modelName;
        }

        public int getCoreCount() {
            return coreCount;
        }

        public List<CoreInfo> getCores() {
            return Collections.unmodifiableList(cores);
        }

        public String getVendor() {
            return vendor;
        }

        public String getModelName() {
            return modelName;
        }
    }

    /**
     * Speicher Info
     */
    public static class MemoryInfo {

        private long totalSize = 0;
        private long freeSize = 0;
        private long usedSize = 0;

        public MemoryInfo(long totalSize, long freeSize, long usedSize) {
            this.totalSize = totalSize;
            this.freeSize = freeSize;
            this.usedSize = usedSize;
        }

        public long getTotalSize() {
            return totalSize;
        }

        public long getFreeSize() {
            return freeSize;
        }

        public long getUsedSize() {
            return usedSize;
        }

        public double getUtilisation() {
            return usedSize * 100 / totalSize;
        }
    }

    /**
     * gibt den Hostnamen zurück
     *
     * @return Hostname
     */
    public static String getHostname() throws IOException {

        if(Files.exists(Paths.get("/proc/sys/kernel/hostname"))) {

            return FileUtil.readFile(Paths.get("/proc/sys/kernel/hostname"));
        }
        return "";
    }

    /**
     * gibt die Kernelversion zurück
     *
     * @return Kernelversion
     */
    public static String getKernelVersion() throws IOException {

        if(Files.exists(Paths.get("/proc/version"))) {

            return FileUtil.readFile(Paths.get("/proc/version"));
        }
        return "";
    }

    /**
     * gibt die Kernelversion zurück
     *
     * @return Kernelversion
     */
    public static double getCoreTemperature() throws IOException {

        if(Files.exists(Paths.get("/sys/class/thermal/thermal_zone0/temp"))) {

            String temperatureStr = FileUtil.readFile(Paths.get("/sys/class/thermal/thermal_zone0/temp"));
            int temperature = Integer.parseInt(temperatureStr.trim());
            return temperature / 1000;
        }
        return 0.0;
    }

    /**
     * gibt die CPU Info zurück
     *
     * @return CPU Info
     */
    public static CpuInfo getCpuInfo() throws IOException {

        int coreCount = Runtime.getRuntime().availableProcessors();
        List<CoreInfo> coreInfoList = new ArrayList<>(coreCount);

        for (int i = 0; i < coreCount; i++) {

            double minClock = Integer.parseInt(FileUtil.readFile(Paths.get("/sys/devices/system/cpu/cpu" + i + "/cpufreq/cpuinfo_min_freq")).trim()) / 1000;
            double currClock = Integer.parseInt(FileUtil.readFile(Paths.get("/sys/devices/system/cpu/cpu" + i + "/cpufreq/scaling_cur_freq")).trim()) / 1000;
            double maxClock = Integer.parseInt(FileUtil.readFile(Paths.get("/sys/devices/system/cpu/cpu" + i + "//cpufreq/cpuinfo_max_freq")).trim()) / 1000;

            coreInfoList.add(new CoreInfo(i, minClock, currClock, maxClock));
        }

        String cpuInfo = FileUtil.readFile(Paths.get("/proc/cpuinfo"));
        String vendor = "", modelName = "";

        Pattern vendorPattern = Pattern.compile("vendor_id\\s*:(.*)");
        Matcher vendorMatcher = vendorPattern.matcher(cpuInfo);
        if(vendorMatcher.find()) {

            vendor = vendorMatcher.group(1).trim();
        }

        Pattern modelNamePattern = Pattern.compile("model\\sname\\s*:(.*)");
        Matcher modelNameMatcher = modelNamePattern.matcher(cpuInfo);
        if(modelNameMatcher.find()) {

            modelName = modelNameMatcher.group(1).trim();
        }

        return new CpuInfo(coreCount, coreInfoList, vendor, modelName);
    }

    /**
     * gibt die Speicher Info zurück
     *
     * @return Speicher Info
     */
    public static MemoryInfo getMemoryInfo() throws IOException {

        String memoryInfo = FileUtil.readFile(Paths.get("/proc/meminfo"));
        long total = 0, free = 0, cached = 0, buffers = 0;

        Pattern totalPattern = Pattern.compile("MemTotal:\\s+(.*)\\s*kB");
        Matcher totalMatcher = totalPattern.matcher(memoryInfo);
        if(totalMatcher.find()) {

            total = Integer.parseInt(totalMatcher.group(1).trim()) * 1000L;
        }
        Pattern freePattern = Pattern.compile("MemFree:\\s+(.*)\\s*kB");
        Matcher freeMatcher = freePattern.matcher(memoryInfo);
        if(freeMatcher.find()) {

            free = Integer.parseInt(freeMatcher.group(1).trim()) * 1000L;
        }
        Pattern cachedPattern = Pattern.compile("Cached:\\s+(.*)\\s*kB");
        Matcher cachedMatcher = cachedPattern.matcher(memoryInfo);
        if(cachedMatcher.find()) {

            cached = Integer.parseInt(cachedMatcher.group(1).trim()) * 1000L;
        }
        Pattern buffersPattern = Pattern.compile("Buffers:\\s+(.*)\\s*kB");
        Matcher buffersMatcher = buffersPattern.matcher(memoryInfo);
        if(buffersMatcher.find()) {

            buffers = Integer.parseInt(buffersMatcher.group(1).trim()) * 1000L;
        }

        return new MemoryInfo(total, free, total - free - cached - buffers);
    }

    /**
     * gibt die System Laufzeit zurück
     *
     * @return
     */
    public static Duration getUptime() throws IOException {

        String fileContent = FileUtil.readFile(Paths.get("/proc/uptime")).trim();
        String[] split = fileContent.split("\\s+");
        long uptime = Math.round(Double.parseDouble(split[0]));
        return Duration.ofSeconds(uptime);
    }

    /**
     * gibt den Zeitstempel des Letzten Systemstartes zurück
     *
     * @return Zeitstempel des Letzten Systemstartes
     */
    public static LocalDateTime getLastStartTime() throws IOException {

        return getLastStartTime(getUptime());
    }

    /**
     * gibt den Zeitstempel des Letzten Systemstartes zurück
     *
     * @param uptime Laufzeit
     * @return Zeitstempel des Letzten Systemstartes
     */
    public static LocalDateTime getLastStartTime(Duration uptime) {

        return LocalDateTime.now().minusSeconds(uptime.getSeconds());
    }
}
