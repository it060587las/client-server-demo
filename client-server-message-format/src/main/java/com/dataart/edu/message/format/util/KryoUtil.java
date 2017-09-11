package com.dataart.edu.message.format.util;

import com.dataart.edu.message.dto.request.AddBirdRequestDto;
import com.dataart.edu.message.dto.request.BaseClientRequestDto;
import com.dataart.edu.message.dto.request.SightingRequestDto;
import com.esotericsoftware.kryo.Kryo;

/**
 * Class which allows to get Kryo instance, bounded to current Thread.
 *
 * @see Information about
 * <a href="https://github.com/EsotericSoftware/kryo">Kryo project</a>.
 * @see ThreadLocal
 * @author alitvinov
 * @version 1.0.0
 * @since 2017-09-07
 */
public final class KryoUtil {

    /**
     * Storage of Kryo instances, which are bounded to Thread.
     */
    private static final ThreadLocal<Kryo> KRYOS = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();                            
            kryo.register(BaseClientRequestDto.class, new BaseClientRequestDto.BaseClientRequestDtoSerializer());
            kryo.register(AddBirdRequestDto.class, new AddBirdRequestDto.AddBirdRequestDtoSerializer());
            kryo.register(SightingRequestDto.class, new SightingRequestDto.SightingRequestDtoSerializer());            
            return kryo;
        }
    ;

    };
    /**
     * Make private in order nobody can create instance of this class.
     */
    private KryoUtil() {
    }

    /**
     * Get Kryo instance, bounded to current Thread.
     *
     * @return Kryo
     * @see Information about
     * <a href="https://github.com/EsotericSoftware/kryo">Kryo project</a>.
     * @see ThreadLocal
     */
    public static Kryo getKryoForThread() {
        return KRYOS.get();
    }
}
