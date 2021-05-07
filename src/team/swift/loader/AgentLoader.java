package team.swift.loader;

import com.sun.tools.attach.VirtualMachine;
import java.io.File;
import java.util.Scanner;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;

public class AgentLoader {
    public static void main(String[] args) throws Exception {
        System.out.println("   _____         _ ______     __                    __         ");
        Thread.sleep(50L);
        System.out.println("  / ___/      __(_) __/ /_   / /   ____  ____ _____/ /__  _____");
        Thread.sleep(50L);
        System.out.println("  \\__ \\ | /| / / / /_/ __/  / /   / __ \\/ __ `/ __  / _ \\/ ___/");
        Thread.sleep(50L);
        System.out.println(" ___/ / |/ |/ / / __/ /_   / /___/ /_/ / /_/ / /_/ /  __/ /    ");
        Thread.sleep(50L);
        System.out.println("/____/|__/|__/_/_/  \\__/  /_____/\\____/\\__,_/\\__,_/\\___/_/     ");

        for(int i = 0; i < "/____/|__/|__/_/_/  \\__/  /_____/\\____/\\__,_/\\__,_/\\___/_/".length(); ++i) {
            System.out.print("*");
            Thread.sleep(10L);
        }

        System.out.println("\n");

        String agentPath = null;
        do {
            System.out.print("Detecting client jar...\n");
            if (new File(System.getProperty("user.dir") + "\\Swift.jar").exists()) {
                System.out.print("Found!\n");
                agentPath = System.getProperty("user.dir") + "\\Swift.jar";
            }
        } while(!(new File(agentPath)).exists());

        System.out.print("Detecting Minecraft JVM process");

        for (int jvmid : MonitoredHost.getMonitoredHost("localhost").activeVms()) {
            MonitoredVm process = MonitoredHost.getMonitoredHost("localhost").getMonitoredVm(new VmIdentifier("//" + jvmid));
            if (MonitoredVmUtil.mainClass(process, true).equals("net.minecraft.client.main.Main")) {
                if (!MonitoredVmUtil.isAttachable(process)) {
                    System.out.println(": Error");
                    throw new Exception("VM is not attachable.");
                }

                System.out.println(": Ok");
                System.out.println("VM PID: " + jvmid);
                System.out.print("Injecting");
                VirtualMachine vm = VirtualMachine.attach(String.valueOf(jvmid));
                vm.loadAgent(agentPath);
                System.out.println(": Ok");
                System.exit(0);
            }
        }

        System.out.print("\nMinecraft VM not found. Please enter the PID: ");
        int pid = (new Scanner(System.in)).nextInt();
        MonitoredVm process = MonitoredHost.getMonitoredHost("localhost").getMonitoredVm(new VmIdentifier("//" + pid));
        if (MonitoredVmUtil.mainClass(process, true).equals("net.minecraft.client.main.Main")) {
            if (!MonitoredVmUtil.isAttachable(process)) {
                System.out.println(": Error");
                throw new Exception("VM is not attachable.");
            }

            System.out.println(": Ok");
            System.out.print("Injecting");
            VirtualMachine vm = VirtualMachine.attach(String.valueOf(pid));
            vm.loadAgent(agentPath);
            System.out.println(": Ok");
            System.exit(0);
        }
    }
}