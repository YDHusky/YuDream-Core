package cn.swustmc.husky.huskyCore.module;

import cn.swustmc.husky.huskyCore.api.command.BaseCommand;
import cn.swustmc.husky.huskyCore.api.command.HuskyCommand;
import cn.swustmc.husky.huskyCore.enums.CommandSenderType;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * cn.swustmc.husky.huskyCore.module
 *
 * @author SiberianHusky
 * * @date 2025/5/19
 */
public class CommandManager {

    static class CommandTree {
        String currentArg;
        Map<String, CommandTree> children = new HashMap<>();
    }

    private final Map<String, Map<String, CommandTree>> commandTreeMap = new HashMap<>();
    private final Map<String, BaseCommand> commandExecutorMap = new HashMap<>();
    private final Map<String, HuskyCommand> huskyCommandExecutorMap = new HashMap<>();

    private void registerCommandTree(JavaPlugin plugin, BaseCommand baseCommand, HuskyCommand huskyCommand) {
        if (!commandTreeMap.containsKey(plugin.getName())) {
            commandTreeMap.put(plugin.getName(), new HashMap<>());
        }
        Map<String, CommandTree> commandTreeMapData = commandTreeMap.get(plugin.getName());
        if (!commandTreeMapData.containsKey(huskyCommand.baseCommand())) {
            commandTreeMapData.put(huskyCommand.baseCommand(), new CommandTree());
            commandTreeMapData.get(huskyCommand.baseCommand()).currentArg = huskyCommand.baseCommand();
        }
        CommandTree cursor = commandTreeMapData.get(huskyCommand.baseCommand());
        for (String arg : huskyCommand.args()) {
            if (!cursor.children.containsKey(arg)) {
                cursor.children.put(arg, new CommandTree());
                cursor.children.get(arg).currentArg = arg;
            }
            cursor = cursor.children.get(arg);
        }
        String commandString = huskyCommand.baseCommand() + "/" + String.join("/", huskyCommand.args());
        commandExecutorMap.put(commandString, baseCommand);
        huskyCommandExecutorMap.put(commandString, huskyCommand);
    }

    public void loadCommand(JavaPlugin plugin, String packageName) {
        try (ScanResult scanResult = new ClassGraph()
                .enableAnnotationInfo()
                .acceptPackages(packageName)
                .scan()) {
            List<ClassInfo> annotatedClasses = scanResult.getClassesWithAnnotation(HuskyCommand.class.getName());
            for (ClassInfo classInfo : annotatedClasses) {
                Class<?> clazz = classInfo.loadClass();
                if (BaseCommand.class.isAssignableFrom(clazz)) {
                    try {
                        BaseCommand commandInstance = (BaseCommand) clazz.getDeclaredConstructor().newInstance();
                        HuskyCommand annotation = clazz.getAnnotation(HuskyCommand.class);
                        String baseCommand = annotation.baseCommand();
                        registerCommandTree(plugin, commandInstance, annotation);
                        plugin.getLogger().info("已加载命令: " + baseCommand);
                    } catch (Exception e) {
                        plugin.getLogger().severe("无法加载命令类: " + clazz.getName());
                        e.printStackTrace();
                    }
                }
            }
        }
        registerCommands(plugin);
        plugin.getLogger().info("所有命令被注册! 已注册" + commandExecutorMap.size() + "个命令!");
    }

    private void registerCommands(JavaPlugin plugin) {
        Map<String, CommandTree> commandTreeMapData = commandTreeMap.get(plugin.getName());
        for (String baseCommand : commandTreeMapData.keySet()) {
            CommandExecutor commandExecutor = (sender, command, label, args) -> {
                StringBuilder commandString = new StringBuilder(baseCommand + "/");
                for (int i = 0; i < args.length; i++) {
                    HuskyCommand huskyCommand = huskyCommandExecutorMap.get(commandString.toString());
                    BaseCommand baseCommandExecutor = commandExecutorMap.get(commandString.toString());

                    commandString.append(args[i]);
                    if (i + 1 != args.length) {
                        commandString.append("/");
                    }
                    if (huskyCommand == null || baseCommandExecutor == null) {
                        continue;
                    }
                    if (huskyCommand.permission().isEmpty() || sender.hasPermission(huskyCommand.permission())) {
                        if (huskyCommand.senderType() == CommandSenderType.ALL) {
                            return baseCommandExecutor.execute(sender, args);
                        } else if (huskyCommand.senderType() == CommandSenderType.CONSOLE) {
                            if (sender instanceof ConsoleCommandSender) {
                                return baseCommandExecutor.execute(sender, args);
                            } else {
                                sender.sendMessage("§c此命令只能由控制台执行");
                                return false;
                            }
                        } else if (huskyCommand.senderType() == CommandSenderType.PLAYER) {
                            if (sender instanceof org.bukkit.entity.Player) {
                                return baseCommandExecutor.execute(sender, args);
                            } else {
                                sender.sendMessage("§c此命令只能由玩家执行");
                                return false;
                            }
                        }
                    } else {
                        sender.sendMessage("§c你没有权限执行此命令");
                        return false;
                    }
                }
                sender.sendMessage("§c未知的命令");
                return false;
            };
            Objects.requireNonNull(plugin.getCommand(baseCommand)).setExecutor(commandExecutor);
            TabCompleter tabCompleter = (sender, command, alias, args) -> {
                CommandTree cursor = commandTreeMapData.get(baseCommand);
                for (int i = 0; i < args.length; i++) {
                    if (cursor.children.containsKey(args[i])) {
                        cursor = cursor.children.get(args[i]);
                        if (i + 1 == args.length) {
                            return new ArrayList<>(cursor.children.keySet());
                        }
                    } else {
                        return new ArrayList<>(cursor.children.keySet());
                    }
                }
                return new ArrayList<>();
            };
            Objects.requireNonNull(plugin.getCommand(baseCommand)).setTabCompleter(tabCompleter);
        }
    }
}
