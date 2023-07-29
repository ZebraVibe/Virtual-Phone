package com.ouken.phone.app.oukenstudioapp.scene.uitls;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 1. Use this to mark i.e. private fields which should be editable/shown in the editor<br>
 * 2. Use this to mark hardcoded scenes to be shown and usable in the editor
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.TYPE})
public @interface Serializable {

}
