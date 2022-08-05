package net.cybercake.worldsx.command;

import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.cybercake.worldsx.Main;
import org.bukkit.GameRule;
import org.bukkit.WorldType;

import java.util.Arrays;
import java.util.Locale;

public class Commodore {

    public static LiteralCommandNode<?> forCommand() {
        return LiteralArgumentBuilder.literal("worldsx")
                .then(LiteralArgumentBuilder.literal("help"))
                .then(LiteralArgumentBuilder.literal("load")
                        .then(
                                load()
                        )
                )
                .then(LiteralArgumentBuilder.literal("delete")
                        .then(RequiredArgumentBuilder.argument("world", StringArgumentType.word()))
                )
                .then(LiteralArgumentBuilder.literal("setspawn")
                        .then(RequiredArgumentBuilder.argument("x", DoubleArgumentType.doubleArg())
                                .then(RequiredArgumentBuilder.argument("y", DoubleArgumentType.doubleArg())
                                        .then(RequiredArgumentBuilder.argument("z", DoubleArgumentType.doubleArg())
                                                .then(RequiredArgumentBuilder.argument("yaw", DoubleArgumentType.doubleArg(-180.0, 180.0))
                                                        .then(RequiredArgumentBuilder.argument("pitch", DoubleArgumentType.doubleArg(-90.0, 90.0))
                                                                .then(RequiredArgumentBuilder.argument("world", StringArgumentType.word()))
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(gamerule())
                .then(LiteralArgumentBuilder.literal("list"))
                .then(LiteralArgumentBuilder.literal("help"))
                .then(LiteralArgumentBuilder.literal("unload")
                        .then(RequiredArgumentBuilder.argument("world", StringArgumentType.word()))
                )
                .then(LiteralArgumentBuilder.literal("tp")
                        .then(RequiredArgumentBuilder.argument("world", StringArgumentType.word())
                                .then(RequiredArgumentBuilder.argument("player", StringArgumentType.word()))
                        )
                )
                .then(LiteralArgumentBuilder.literal("reload"))


                .build();
    }

    private static RequiredArgumentBuilder<Object, ?> load() {
        RequiredArgumentBuilder<Object, ?> worldType = RequiredArgumentBuilder.argument("worldName", StringArgumentType.word());
        Arrays.stream(WorldType.values())
                .map(type -> type.getName().toLowerCase(Locale.ROOT))
                .forEach(type ->
                        worldType.then(
                                LiteralArgumentBuilder.literal(type)
                        )
                );

        return worldType;
    }

    private static LiteralArgumentBuilder<Object> gamerule() {
        LiteralArgumentBuilder<Object> gamerule = LiteralArgumentBuilder.literal("gamerule");
        for(GameRule<?> gameRule : GameRule.values()) {
            ArgumentType<?> argumentType;
            if(gameRule.getType() == Boolean.class)
                argumentType = (Main.BOOLEAN_FALSE.equalsIgnoreCase("false") && Main.BOOLEAN_TRUE.equalsIgnoreCase("true") ? BoolArgumentType.bool() : StringArgumentType.word());
            else if(gameRule.getType() == Integer.class)
                argumentType = IntegerArgumentType.integer();
            else
                argumentType = StringArgumentType.word();

            gamerule.then(LiteralArgumentBuilder.literal(gameRule.getName())
                    .then(RequiredArgumentBuilder.argument("value", argumentType)
                            .then(RequiredArgumentBuilder.argument("world", StringArgumentType.word()))
                    )
            );
        }
        return gamerule;
    }

}
