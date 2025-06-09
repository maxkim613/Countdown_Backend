package back.mapper;



import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

@Mapper
public interface CertiMapper {

    void insertCertiInfo(
    		
	 @Param("uuid") String uuid,
    		             
     @Param("username") String username,

     @Param("email") String email,
     
     @Param("certiNum") String certiNum
	 
	 );

	 String findCertiNumByEmail(
			 
	 @Param("email") String email
	 
	 );

	
}
