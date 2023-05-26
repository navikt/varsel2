package no.nav.modig.core.context;

import java.io.Serializable;

/**
 * Represents an attribute contained within a <code>Context</code>.
 * 
 * <p>
 * The choice of a certain interface to represent attributes is to ensure type safety while maintaining loose-coupling as
 * attributes can be added or removed to the system.
 * 
 * <p>
 * <b> The implementors should be either immutable or thread safe.</b>
 * 
 * @author Nader Aeinehchi
 * 
 */
public interface Attribute extends Serializable {
}