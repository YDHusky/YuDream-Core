package cn.swustmc.yudream.yudreamCore.module;

import cn.swustmc.yudream.yudreamCore.api.command.BaseCommand;
import cn.swustmc.yudream.yudreamCore.api.command.YuDreamCommand;
import cn.swustmc.yudream.yudreamCore.common.utils.CommandUtils;
import cn.swustmc.yudream.yudreamCore.enums.CommandSenderType;
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

    private static CommandManager instance;

    public static CommandManager getInstance() {
        if (instance == null) {
            instance = new CommandManager();
        }
        return instance;
    }

    static class CommandTree {
        String currentArg;
        Map<String, CommandTree> children = new HashMap<>();
    }


    private final Map<String, Map<String, CommandTree>> commandTreeMap = new HashMap<>();
    private final Map<String, BaseCommand> commandExecutorMap = new HashMap<>();
    private final Map<String, YuDreamCommand> yudreamCommandExecutorMap = new HashMap<>();

    private void registerCommandTree(JavaPlugin plugin, BaseCommand baseCommand, YuDreamCommand yuDreamCommand) {
        if (!commandTreeMap.containsKey(plugin.getName())) {
            commandTreeMap.put(plugin.getName(), new HashMap<>());
        }
        Map<String, CommandTree> commandTreeMapData = commandTreeMap.get(plugin.getName());
        if (!commandTreeMapData.containsKey(yuDreamCommand.baseCommand())) {
            commandTreeMapData.put(yuDreamCommand.baseCommand(), new CommandTree());
            commandTreeMapData.get(yuDreamCommand.baseCommand()).currentArg = yuDreamCommand.baseCommand();
        }
        CommandTree cursor = commandTreeMapData.get(yuDreamCommand.baseCommand());
        for (String arg : yuDreamCommand.args()) {
            if (!cursor.children.containsKey(arg)) {
                cursor.children.put(arg, new CommandTree());
                cursor.children.get(arg).currentArg = arg;
            }
            cursor = cursor.children.get(arg);
        }
        String commandString = yuDreamCommand.baseCommand() + "/" + String.join("/", yuDreamCommand.args());
        commandExecutorMap.put(commandString, baseCommand);
        yudreamCommandExecutorMap.put(commandString, yuDreamCommand);
    }

    public void loadCommand(JavaPlugin plugin, String packageName) {
        try (ScanResult scanResult = new ClassGraph()
                .enableAnnotationInfo()
                .acceptPackages(packageName)
                .scan()) {
            List<ClassInfo> annotatedClasses = scanResult.getClassesWithAnnotation(YuDreamCommand.class.getName());
            for (ClassInfo classInfo : annotatedClasses) {
                Class<?> clazz = classInfo.loadClass();
                if (BaseCommand.class.isAssignableFrom(clazz)) {
                    try {
                        BaseCommand commandInstance = (BaseCommand) clazz.getDeclaredConstructor().newInstance();
                        YuDreamCommand annotation = clazz.getAnnotation(YuDreamCommand.class);
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
                    YuDreamCommand yuDreamCommand = yudreamCommandExecutorMap.get(commandString.toString());
                    BaseCommand baseCommandExecutor = commandExecutorMap.get(commandString.toString());

                    commandString.append(args[i]);
                    if (i + 1 != args.length) {
                        commandString.append("/");
                    }
                    if (yuDreamCommand == null || baseCommandExecutor == null) {
                        continue;
                    }
                    if (yuDreamCommand.permission().isEmpty() || sender.hasPermission(yuDreamCommand.permission())) {
                        if (yuDreamCommand.senderType() == CommandSenderType.ALL) {
                            return baseCommandExecutor.execute(sender, args);
                        } else if (yuDreamCommand.senderType() == CommandSenderType.CONSOLE) {
                            if (sender instanceof ConsoleCommandSender) {
                                return baseCommandExecutor.execute(sender, args);
                            } else {
                                sender.sendMessage("§c此命令只能由控制台执行");
                                return false;
                            }
                        } else if (yuDreamCommand.senderType() == CommandSenderType.PLAYER) {
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
                if (args.length > 0 && args[0].equals("help")) {
                    List<BaseCommand> commandList = new ArrayList<>();

                    for (String commandStr : yudreamCommandExecutorMap.keySet()) {
                        if (commandStr.startsWith(baseCommand + "/")) {
                            commandList.add(commandExecutorMap.get(commandStr));
                        }
                    }
                    sender.sendMessage(CommandUtils.commandHelpMenu(plugin, commandList));
                    return true;
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
