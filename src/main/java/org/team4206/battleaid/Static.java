
package org.team4206.battleaid;

import java.io.File;

import edu.wpi.first.wpilibj.Filesystem;

public final class Static {
    public static final String CONFIG_DIR = Filesystem.getDeployDirectory().toString()
            + File.separator
            + "configuration";
}