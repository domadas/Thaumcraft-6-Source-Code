// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.render.uv;

import thaumcraft.codechicken.lib.vec.ITransformation;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import thaumcraft.codechicken.lib.math.MathHelper;

public class UVTranslation extends UVTransformation
{
    public double du;
    public double dv;
    
    public UVTranslation(final double u, final double v) {
        du = u;
        dv = v;
    }
    
    @Override
    public void apply(final UV uv) {
        uv.u += du;
        uv.v += dv;
    }
    
    @Override
    public UVTransformation at(final UV point) {
        return this;
    }
    
    @Override
    public UVTransformation inverse() {
        return new UVTranslation(-du, -dv);
    }
    
    @Override
    public UVTransformation merge(final UVTransformation next) {
        if (next instanceof UVTranslation) {
            final UVTranslation t = (UVTranslation)next;
            return new UVTranslation(du + t.du, dv + t.dv);
        }
        return null;
    }
    
    @Override
    public boolean isRedundant() {
        return MathHelper.between(-1.0E-5, du, 1.0E-5) && MathHelper.between(-1.0E-5, dv, 1.0E-5);
    }
    
    @Override
    public String toString() {
        final MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "UVTranslation(" + new BigDecimal(du, cont) + ", " + new BigDecimal(dv, cont) + ")";
    }
}
