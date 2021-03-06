package grphaqladapter.adaptedschemabuilder.validator;


import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.TypeAnnotations;
import grphaqladapter.annotations.*;

import java.lang.reflect.Modifier;

public final class TypeValidator {

    public final static void validate(MappedClass mappedClass)
    {


        Assert.ifNull(mappedClass.baseClass() , "base class is null");
        Assert.ifModifierNotValidForATypeClass(mappedClass.baseClass());
        Assert.ifNameNotValid(mappedClass.typeName());
        Assert.ifConditionTrue("an union mapped class can not contains mapped methods" ,
                mappedClass.mappedType().is(MappedClass.MappedType.UNION),
                mappedClass.mappedMethods().size()>0);
        Assert.ifConditionTrue("just interfaces can map to union" ,
                !mappedClass.baseClass().isInterface() ,
                mappedClass.mappedType().is(MappedClass.MappedType.UNION));
        Assert.ifConditionTrue("just interfaces can map to interface" ,
                !mappedClass.baseClass().isInterface() ,
                mappedClass.mappedType().is(MappedClass.MappedType.INTERFACE));
        Assert.ifConditionTrue("just a class with public default constructor can map to input type" ,
                mappedClass.baseClass().isInterface()||mappedClass.baseClass().isEnum()||
                        !Modifier.isPublic(mappedClass.baseClass().getModifiers())||
                        !Assert.hasDefaultPublicConstructor(mappedClass.baseClass()) ,
                mappedClass.mappedType().is(MappedClass.MappedType.INPUT_TYPE));


        Assert.ifConditionTrue("just a class with public default constructor can map to query type" ,
                mappedClass.baseClass().isInterface()||mappedClass.baseClass().isEnum()||
                        !Modifier.isPublic(mappedClass.baseClass().getModifiers())||
                        !Assert.hasDefaultPublicConstructor(mappedClass.baseClass()) ,
                mappedClass.mappedType().is(MappedClass.MappedType.QUERY));

        Assert.ifConditionTrue("just a class with public default constructor can map to mutation type" ,
                mappedClass.baseClass().isInterface()||mappedClass.baseClass().isEnum()||
                        !Modifier.isPublic(mappedClass.baseClass().getModifiers())||
                        !Assert.hasDefaultPublicConstructor(mappedClass.baseClass()) ,
                mappedClass.mappedType().is(MappedClass.MappedType.MUTATION));

        Assert.ifConditionTrue("just a class with public default constructor can map to subscription type" ,
                mappedClass.baseClass().isInterface()||mappedClass.baseClass().isEnum()||
                        !Modifier.isPublic(mappedClass.baseClass().getModifiers())||
                        !Assert.hasDefaultPublicConstructor(mappedClass.baseClass()) ,
                mappedClass.mappedType().is(MappedClass.MappedType.SUBSCRIPTION));

        Assert.ifConditionTrue("just a class with public default constructor can map to object-type" ,
                mappedClass.baseClass().isInterface() || mappedClass.baseClass().isEnum() ||
                !Modifier.isPublic(mappedClass.baseClass().getModifiers()) ||
                !Assert.hasDefaultPublicConstructor(mappedClass.baseClass()) ,
                mappedClass.mappedType().is(MappedClass.MappedType.OBJECT_TYPE));


        for(MappedMethod method:mappedClass.mappedMethods().values())
        {
            FieldValidator.validate(method,mappedClass.mappedType().is(MappedClass.MappedType.INTERFACE)||
                    mappedClass.mappedType().isTopLevelType());
            Assert.ifMethodNotMemberOfClass(method.method() , mappedClass.baseClass());
        }

    }

    public final static void validate(GraphqlTypeAnnotation annotation , boolean skipIfNull)
    {
        if(annotation==null && skipIfNull)
            return;
        Assert.ifNull(annotation , "provided annotation is null");
        if(Assert.isNoNullString(annotation.typeName()))
            Assert.ifNameNotValid(annotation.typeName());
    }
    public final static void validate(GraphqlTypeAnnotation annotation)
    {
        validate(annotation , false);
    }


    public final static void validate(GraphqlInputTypeAnnotation annotation , boolean skipIfNull)
    {
        if(annotation==null && skipIfNull)
            return;
        Assert.ifNull(annotation , "provided annotation is null");
        if(Assert.isNoNullString(annotation.typeName()))
            Assert.ifNameNotValid(annotation.typeName());
    }
    public final static void validate(GraphqlInputTypeAnnotation annotation)
    {
        validate(annotation , false);
    }


    public final static void validate(GraphqlUnionAnnotation annotation , boolean skipIfNull)
    {
        if(annotation==null && skipIfNull)
            return;
        Assert.ifNull(annotation , "provided annotation is null");
        if(Assert.isNoNullString(annotation.typeName()))
            Assert.ifNameNotValid(annotation.typeName());
    }
    public final static void validate(GraphqlUnionAnnotation annotation)
    {
        validate(annotation , false);
    }


    public final static void validate(GraphqlInterfaceAnnotation annotation , boolean skipIfNull)
    {
        if(annotation==null && skipIfNull)
            return;

        Assert.ifNull(annotation , "provided annotation is null");
        if(Assert.isNoNullString(annotation.typeName()))
            Assert.ifNameNotValid(annotation.typeName());
    }

    public final static void validate(GraphqlInterfaceAnnotation annotation)
    {
        validate(annotation , false);
    }

    public final static void validate(GraphqlEnumAnnotation annotation , boolean skipIfNull)
    {
        if(annotation==null && skipIfNull)
            return;

        Assert.ifNull(annotation , "provided annotation is null");
        if(Assert.isNoNullString(annotation.typeName()))
            Assert.ifNameNotValid(annotation.typeName());

    }
    public final static void validate(GraphqlEnumAnnotation annotation)
    {

        validate(annotation , false);
    }





    public final static void validate(Class cls , TypeAnnotations annotations)
    {
        Assert.ifNull(cls , "provided class is null");
        Assert.ifNull(annotations , "provided annotations is null");
        Assert.ifModifierNotValidForATypeClass(cls);

        if(cls.isInterface())
        {
            //so handle it please !
            Assert.ifNotNull(annotations.typeAnnotation() , "an interface can not map to GraphqlType ["+cls+"]");
            Assert.ifNotNull(annotations.inputTypeAnnotation() , "an interface can not map to GraphqlInputType ["+cls+"]");
            Assert.ifNotNull(annotations.enumAnnotation() , "an interface can not map to GraphqlEnum ["+cls+"]" );
            Assert.ifNotNull(annotations.queryAnnotation() , "an interface can not map to GraphqlQuery ["+cls+"]");
            Assert.ifNotNull(annotations.mutationAnnotation() , "an interface can not map to GraphqlMutation ["+cls+"]");
            Assert.ifNotNull(annotations.subscriptionAnnotation() , "an interface can not map to GraphqlSubscription ["+cls+"]");

            Assert.ifConditionTrue("an interface can not map to both GraphqlUnion and GraphqlInterface ["+cls+"]" ,
                    annotations.unionAnnotation()!=null , annotations.interfaceAnnotation()!=null);
            Assert.ifConditionTrue("no annotation found for ["+cls+"]" ,
                    annotations.unionAnnotation()==null , annotations.interfaceAnnotation()==null);

            if(annotations.interfaceAnnotation()!=null)
            {
                //so mapped to interface
                validate(annotations.interfaceAnnotation());
            }else
            {
                validate(annotations.unionAnnotation());
            }

        }else if(Modifier.isAbstract(cls.getModifiers()))
        {
            Assert.ifNotNull(annotations.interfaceAnnotation() ,"an abstract class can not map to GraphqlInterface ["+cls+"]");
            Assert.ifNotNull(annotations.inputTypeAnnotation() , "an abstract class can not map to GraphqlInputType ["+cls+"]");
            Assert.ifNotNull(annotations.enumAnnotation() , "an abstract class can not map to GraphqlEnum ["+cls+"]");
            Assert.ifNotNull(annotations.unionAnnotation() , "an abstract class can not map to GraphqlUnion ["+cls+"]");
            Assert.ifNotNull(annotations.queryAnnotation() , "an abstract class can not map to GraphqlQuery ["+cls+"]");
            Assert.ifNotNull(annotations.mutationAnnotation() , "an abstract class can not map to GraphqlMutation ["+cls+"]");
            Assert.ifNotNull(annotations.subscriptionAnnotation() , "an abstract class can not map to GraphqlSubscription ["+cls+"]");

            Assert.ifNull(annotations.typeAnnotation() , "no annotation found for ["+cls+"]");

            validate(annotations.typeAnnotation());

        }else if(cls.isEnum())
        {
            Assert.ifNotNull(annotations.typeAnnotation() , "an enum class can not map to GraphqlType ["+cls+"]");
            Assert.ifNotNull(annotations.inputTypeAnnotation() , "an enum class can not map to GraphqlInputType ["+cls+"]");
            Assert.ifNotNull(annotations.interfaceAnnotation() , "an enum class can not map to GraphqlInterface ["+cls+"]");
            Assert.ifNotNull(annotations.unionAnnotation() , "an enum class can not map to GraphqlUnion ["+cls+"]");
            Assert.ifNotNull(annotations.queryAnnotation() , "an enum class can not map to GraphqlQuery ["+cls+"]");
            Assert.ifNotNull(annotations.mutationAnnotation() , "an enum class can not map to GraphqlMutation ["+cls+"]");
            Assert.ifNotNull(annotations.subscriptionAnnotation() , "an enum class can not map to GraphqlSubscription ["+cls+"]");


            Assert.ifNull(annotations.enumAnnotation() , "no annotation found for ["+cls+"]");

            validate(annotations.enumAnnotation());

        }else
        {
            Assert.ifNotNull(annotations.unionAnnotation() , "a class can not map to GraphqlUnion ["+cls+"]");
            Assert.ifNotNull(annotations.interfaceAnnotation() , "a class can not map to GraphqlInterface ["+cls+"]");
            Assert.ifNotNull(annotations.enumAnnotation() , "a class can not map to GraphqlEnum ["+cls+"]");
            Assert.ifConditionTrue( "no annotation found for ["+cls+"]" ,
                    annotations.inputTypeAnnotation()==null ,
                    annotations.typeAnnotation()==null ,
                    annotations.queryAnnotation()==null ,
                    annotations.mutationAnnotation()==null ,
                    annotations.subscriptionAnnotation()==null);

            GraphqlInputTypeAnnotation inputTypeAnnotation = annotations.inputTypeAnnotation();
            GraphqlTypeAnnotation typeAnnotation = annotations.typeAnnotation();
            GraphqlQueryAnnotation queryAnnotation = annotations.queryAnnotation();
            GraphqlMutationAnnotation mutationAnnotation = annotations.mutationAnnotation();
            GraphqlSubscriptionAnnotation subscriptionAnnotation = annotations.subscriptionAnnotation();

            Assert.ifConditionTrue("just one of GraphqlQuery , GraphqlMutation and GraphqlSubscription can present ["+cls+"]" ,
                    queryAnnotation!=null && mutationAnnotation!=null && subscriptionAnnotation!=null ,
                    queryAnnotation!=null && mutationAnnotation!=null ,
                    queryAnnotation!=null && subscriptionAnnotation!=null ,
                    mutationAnnotation!=null && subscriptionAnnotation!=null);

            final boolean topLevelType = queryAnnotation!=null || mutationAnnotation!=null || subscriptionAnnotation!=null;

            Assert.ifConditionTrue("a top level type [Query,Mutation,Subscription] , can not map to other types ["+cls+"]" ,
                    topLevelType ,
                    inputTypeAnnotation!=null && typeAnnotation!=null);

            if(topLevelType)
            {
                //so hndle it later please !
            }else if(inputTypeAnnotation!=null && typeAnnotation!=null)
            {
                String inputTypeName =
                        Assert.isNullString(inputTypeAnnotation.typeName())?cls.getSimpleName():inputTypeAnnotation.typeName();
                String typeName =
                        Assert.isNullString(typeAnnotation.typeName())?cls.getSimpleName():typeAnnotation.typeName();

                if(inputTypeName.equals(typeName))
                {
                    throw new IllegalStateException("GraphqlType and GraphqlType have same typeName [typeName:"+inputTypeName+" , class:"+cls+"]");
                }

                Assert.ifModifierNotValidForAnInputTypeClass(cls);
            }else if(inputTypeAnnotation!=null)
            {
                Assert.ifModifierNotValidForAnInputTypeClass(cls);
            }

        }
    }

    public final static void validate(GraphqlQueryAnnotation annotation , boolean skipIfNull)
    {
        if(annotation==null && skipIfNull)
            return;
        Assert.ifNull(annotation , "provided annotation is null");
        if(Assert.isNoNullString(annotation.typeName()))
            Assert.ifNameNotValid(annotation.typeName());
    }

    public final static void validate(GraphqlQueryAnnotation annotation)
    {
        validate(annotation , false);
    }

    public final static void validate(GraphqlMutationAnnotation annotation , boolean skipIfNull)
    {
        if(annotation==null && skipIfNull)
            return;
        Assert.ifNull(annotation , "provided annotation is null");
        if(Assert.isNoNullString(annotation.typeName()))
            Assert.ifNameNotValid(annotation.typeName());
    }

    public final static void validate(GraphqlMutationAnnotation annotation)
    {
        validate(annotation , false);
    }

    public final static void validate(GraphqlSubscriptionAnnotation annotation , boolean skipIfNull)
    {
        if(annotation==null && skipIfNull)
            return;
        Assert.ifNull(annotation , "provided annotation is null");
        if(Assert.isNoNullString(annotation.typeName()))
            Assert.ifNameNotValid(annotation.typeName());
    }

    public final static void validate(GraphqlSubscriptionAnnotation annotation)
    {
        validate(annotation ,false);
    }
}
