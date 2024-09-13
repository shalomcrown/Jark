/*
 * Copyright notice
 * This code is not covered by any copyright
 *
 * In no event shall the author(s) be liable for any special, direct, indirect, consequential,
 *  or incidental damages or any damages whatsoever, whether in an action of contract,
 *  negligence or other tort, arising out of or in connection with the use of the code or the
 *  contents of the code
 *
 *  All information in the code is provided "as is" with no guarantee of completeness, accuracy,
 *   timeliness or of the results obtained from the use of this code, and without warranty of any
 *   kind, express or implied, including, but not limited to warranties of performance,
 *   merchantability and fitness for a particular purpose.
 *
 *  The author(s) will not be liable to You or anyone else for any decision made or action
 *  taken in reliance on the information given by the code or for any consequential, special
 *  or similar damages, even if advised of the possibility of such damages.
 *
 *
 */

package com.kirayim.jark.beans;

import java.util.Map;

/**
 * Implement this interface on classes that need custome conversion to and from HTML<p>
 *
 */
public interface IBeanConverter {

    void serialize(BeanFormItemInfo info, StringBuilder html, Object value);
    void deserialize(BeanFormItemInfo info, Map<String, String> bodyMap);

}
