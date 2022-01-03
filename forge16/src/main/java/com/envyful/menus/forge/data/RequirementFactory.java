package com.envyful.menus.forge.data;

import com.envyful.menus.forge.MenusForge;
import com.envyful.menus.forge.data.data.Requirement;
import com.envyful.menus.forge.data.impl.GroovyPlaceholderRequirement;
import com.google.common.collect.Maps;
import org.atteo.classindex.ClassIndex;
import org.spongepowered.configurate.ConfigurationNode;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.function.Function;

public class RequirementFactory {

    private static final Map<String, RequirementInfo> ITEM_REQUIREMENTS = Maps.newConcurrentMap();

    public static void init() {
        for (Class<?> clazz : ClassIndex.getAnnotated(Requirement.class, MenusForge.getInstance().getClass().getClassLoader())) {
            if (!(ItemRequirement.class.isAssignableFrom(clazz))) {
                continue;
            }

            Requirement requirement = clazz.getAnnotation(Requirement.class);
            Constructor<ItemRequirement> constructor = getConstructor(clazz);

            if (constructor == null) {
                continue;
            }

            if (GroovyPlaceholderRequirement.class.isAssignableFrom(clazz)) {
                if (!isPlaceholderEnabled()) {
                    continue;
                }
            }

            ITEM_REQUIREMENTS.put(requirement.value(), new RequirementInfo(
                    requirement.value(),
                    configurationNode -> {
                        try {
                            return constructor.newInstance(configurationNode);
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
            ));
        }
    }

    private static Constructor<ItemRequirement> getConstructor(Class<?> clazz) {
        try {
            return (Constructor<ItemRequirement>) clazz.getConstructor(ConfigurationNode.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static boolean isPlaceholderEnabled() {
        try {
            Class.forName("com.envyful.papi.forge.ForgePlaceholderAPI");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static ItemRequirement from(ConfigurationNode node) {
        String typeData = node.node("type").getString("none");

        if (typeData.equalsIgnoreCase("none")) {
            return null;
        }

        RequirementInfo type = ITEM_REQUIREMENTS.get(typeData);

        if (type == null) {
            return null;
        }

        return type.getConstructor().apply(node);
    }

    public static class RequirementInfo {

        private final String type;
        private final Function<ConfigurationNode, ItemRequirement> constructor;

        public RequirementInfo(String type, Function<ConfigurationNode, ItemRequirement> constructor) {
            this.type = type;
            this.constructor = constructor;
        }

        public String getType() {
            return this.type;
        }

        public Function<ConfigurationNode, ItemRequirement> getConstructor() {
            return this.constructor;
        }
    }
}
