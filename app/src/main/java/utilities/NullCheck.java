package utilities;

import java.util.Objects;

public class NullCheck {

    public NullCheck () {
        throw new IllegalArgumentException("Instatiation is not allowed. Nice try.");
    }

    /**
     * return true if Object obj IS NULL
     * @param obj
     * @return boolean
     */
    public static boolean isNull(Object obj){
        return (obj == null || obj.toString().isBlank());
    }

    /**
     * returns true if Object obj is NOT NULL.
     * @param obj
     * @return boolean
     */
    public static boolean isNotNull(Object obj){
        try {
            return (obj != null || !obj.toString().isBlank());
        } catch (NullPointerException e){
            return false;
        }
    }
}
