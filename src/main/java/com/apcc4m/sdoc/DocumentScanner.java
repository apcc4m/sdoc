package com.apcc4m.sdoc;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apcc4m.sdoc.annotation.Sdoc;
import com.apcc4m.sdoc.bean.Documentation;
import com.apcc4m.sdoc.bean.Options;
import com.apcc4m.sdoc.bean.Parameter;
import com.apcc4m.sdoc.bean.RequestHandler;
import com.apcc4m.sdoc.bean.Response;
import com.apcc4m.sdoc.bean.Tag;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;

@Component
public class DocumentScanner implements ApplicationListener<ContextRefreshedEvent> {

    private final List<RequestMappingInfoHandlerMapping> handlerMappings;
    private List<RequestHandler> requestHandlers;
    private List<Documentation> documentations;
    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Autowired
    private List<SdocInfo> sdocInfoList;

    @Autowired
    public DocumentScanner(List<RequestMappingInfoHandlerMapping> handlerMappings) {
        this.handlerMappings = handlerMappings;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        requestHandlers = requestHandlers();
        documentations = new ArrayList<Documentation>();
        for (SdocInfo sdocInfo : sdocInfoList) {
            Documentation documentation = new Documentation();
            Map<String, List<Options>> optionsMap = new HashMap<String, List<Options>>();
            for (RequestHandler item : requestHandlers) {
                String className = item.getHandlerMethod().getBeanType().getName();
                if (sdocInfo.getScannerPath() != null) {
                    if (!className.contains(sdocInfo.getScannerPath())) {
                        continue;
                    }
                }
                String beanName = item.getHandlerMethod().getBean().toString();
                createOptionsMap(item, beanName, optionsMap);
                // 初始化参数
                MethodParameter[] methodParameters = item.getHandlerMethod().getMethodParameters();
                List<Options> optionsList = optionsMap.get(beanName);
                Options lastOptions = optionsList.get(optionsList.size() - 1);
                List<Parameter> parameters = new ArrayList<Parameter>();
                for (int i = 0; i < methodParameters.length; i++) {
                    Class<?> pclass = methodParameters[i].getParameterType();
                    Parameter p = createRarameter(methodParameters[i], pclass);
                    if (p != null) {
                        parameters.add(p);
                    }
                }
                lastOptions.setParameters(parameters);

                // 初始化返回值
                Class<?> returnClass = item.getHandlerMethod().getMethod().getReturnType();
                Type returnType = item.getHandlerMethod().getMethod().getGenericReturnType();
                lastOptions.setResponse(createResponse(returnClass, returnType));
            }
            documentation.setTags(createTags(optionsMap));
            documentation.setOptionsMap(optionsMap);
            documentation.setGroupId(sdocInfo.hashCode());
            documentation.setGroupName(sdocInfo.getGroupName());
            documentations.add(documentation);
        }

    }

    public List<RequestHandler> getRequestHandlers() {
        return requestHandlers;
    }

    public List<Documentation> getDocumentations() {
        return documentations;
    }

    public List<RequestHandler> requestHandlers() {
        return byPatternsCondition().sortedCopy(from(nullToEmptyList(handlerMappings))
                .transformAndConcat(toMappingEntries()).transform(toRequestHandler()));
    }

    public static Ordering<RequestHandler> byPatternsCondition() {
        return Ordering.from(new Comparator<RequestHandler>() {
            @Override
            public int compare(RequestHandler first, RequestHandler second) {
                return first.getRequestMapping().getPatternsCondition().toString()
                        .compareTo(second.getRequestMapping().getPatternsCondition().toString());
            }
        });
    }

    public static <T> List<T> nullToEmptyList(Collection<T> newValue) {
        if (newValue == null) {
            return newArrayList();
        }
        return newArrayList(newValue);
    }

    private Function<? super RequestMappingInfoHandlerMapping, Iterable<Map.Entry<RequestMappingInfo, HandlerMethod>>> toMappingEntries() {
        return new Function<RequestMappingInfoHandlerMapping, Iterable<Map.Entry<RequestMappingInfo, HandlerMethod>>>() {
            @Override
            public Iterable<Map.Entry<RequestMappingInfo, HandlerMethod>> apply(
                    RequestMappingInfoHandlerMapping input) {
                return input.getHandlerMethods().entrySet();
            }
        };
    }

    private Function<Map.Entry<RequestMappingInfo, HandlerMethod>, RequestHandler> toRequestHandler() {
        return new Function<Map.Entry<RequestMappingInfo, HandlerMethod>, RequestHandler>() {
            @Override
            public RequestHandler apply(Map.Entry<RequestMappingInfo, HandlerMethod> input) {
                return new RequestHandler(input.getKey(), input.getValue());
            }
        };
    }

    public List<Tag> createTags(Map<String, List<Options>> apiMap) {
        List<Tag> tags = new ArrayList<Tag>();
        for (String key : apiMap.keySet()) {
            Tag tag = new Tag();
            tag.setTagId(key);
            tag.setTagName(key);
            List<Options> list = apiMap.get(key);
            List<Tag> children = new ArrayList<Tag>();
            Integer apiIndex = 0;
            for (Options api : list) {
                Tag child = new Tag();
                child.setTagId(apiIndex.toString());
                child.setTagName(api.getPath());
                child.setTagMethod(api.getMethod());
                child.setTagSummary(api.getSummary());
                apiIndex++;
                children.add(child);
            }
            tag.setChildren(children);
            tags.add(tag);
        }
        return tags;
    }

    public void createOptionsMap(RequestHandler item, String beanName, Map<String, List<Options>> optionsMap) {
        List<Options> optionsList = optionsMap.get(beanName);
        if (optionsList == null) {
            optionsList = new ArrayList<Options>();
        }
        Options apiListing = new Options();
        Set<String> parrerns = item.getRequestMapping().getPatternsCondition().getPatterns();
        Set<RequestMethod> methods = item.getRequestMapping().getMethodsCondition().getMethods();
        Set<MediaType> consumes = item.getRequestMapping().getConsumesCondition().getConsumableMediaTypes();
        Set<MediaType> produces = item.getRequestMapping().getProducesCondition().getProducibleMediaTypes();
        Sdoc sdoc = item.getHandlerMethod().getMethodAnnotation(Sdoc.class);
        if (sdoc != null) {
            apiListing.setSummary(sdoc.value());
            apiListing.setDescription(sdoc.notes());
        } else {
            String operationName = item.getHandlerMethod().getMethod().getName();
            apiListing.setSummary(operationName);
        }
        // 路径
        for (String parrern : parrerns) {
            apiListing.setPath(parrern);
        }
        // 方法
        for (RequestMethod method : methods) {
            apiListing.setMethod(method.name());
        }
        // 请求类型
        for (MediaType mediaType : consumes) {
            apiListing.setConsumes(mediaType.toString());
        }
        // 返回类型
        for (MediaType mediaType : produces) {
            apiListing.setProduces(mediaType.toString());
        }
        optionsList.add(apiListing);
        optionsMap.put(beanName, optionsList);
    }

    public Response createResponse(Class<?> clazz, Type type) {
        Response response = new Response();
        response.setName(clazz.getName());
        response.setDescription(clazz.getName());

        if (clazz == List.class) {
            StringBuilder result = new StringBuilder();
            if (type instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) type;
                Type[] actualTypes = paramType.getActualTypeArguments();
                // TODO 获取泛型类型，设想只有一个，此处是个数组，有待观察
                if (actualTypes.length == 0) {
                    result.append("[]");
                } else {
                    result.append("[");
                    if (actualTypes[0] instanceof Class) {
                        Class<?> clz = (Class<?>) actualTypes[0];
                        List<Class<?>> parents = new ArrayList<>();
                        String s = formatClass1(clz, parents);
                        result.append(s).append("]");
                    } else {
                        result.append("]");
                    }
                }
            } else {
                result.append("[]");
            }
            response.setValue(result.toString());
            return response;
        }

        if (isIgnoreResponseClass(clazz)) {
            response.setType(clazz.getName());
            response.setValue(clazz.getName());
            return response;
        }

        if (isBaseClass(clazz)) {
            response.setType(clazz.getName());
            response.setValue(clazz.getName());
            return response;
        }
        List<Class<?>> parents = new ArrayList<>();
        parents.add(clazz);
        String value = formatClass(clazz, parents);
        response.setType("body");
        response.setValue(value);
        return response;
    }

    public boolean isBaseClass(Class<?> clazz) {
        if (clazz == int.class || clazz == Integer.class || clazz == long.class || clazz == Long.class
                || clazz == double.class || clazz == Double.class || clazz == float.class || clazz == Float.class) {
            return true;
        } else if (clazz == String.class) {
            return true;
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            return true;
        } else if (clazz == java.util.Date.class || clazz == java.sql.Date.class) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isIgnoreRequestClass(Class<?> clazz) {
        if (clazz == HttpServletRequest.class || clazz == HttpSession.class || clazz == RedirectAttributes.class
                || clazz == MultipartHttpServletRequest.class || clazz == HttpServletResponse.class
                || clazz == Map.class || clazz == List.class) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isIgnoreResponseClass(Class<?> clazz) {
        if (clazz == ModelAndView.class || clazz == Map.class || clazz == Object.class) {
            return true;
        } else {
            return false;
        }
    }

    public Parameter createRarameter(MethodParameter methodParameter, Class<?> clazz) {
        // 判断是否是需要忽略的参数
        if (isIgnoreRequestClass(clazz)) {
            return null;
        }
        Parameter parameter = new Parameter();
        methodParameter.initParameterNameDiscovery(parameterNameDiscoverer);
        String name = methodParameter.getParameterName();
        parameter.setName(name);
        parameter.setDescription(name);
        parameter.setType(clazz.getSimpleName());

        PathVariable pathVariable = methodParameter.getParameterAnnotation(PathVariable.class);
        if (pathVariable != null) {
            parameter.setAnnotationType("PathVariable");
            if (!StringUtils.isEmpty(pathVariable.value())) {
                parameter.setName(pathVariable.value());
            }
            return parameter;
        }

        RequestParam requestParam = methodParameter.getParameterAnnotation(RequestParam.class);
        if (requestParam != null) {
            parameter.setAnnotationType("RequestParam");
            if (!StringUtils.isEmpty(requestParam.value())) {
                parameter.setName(requestParam.value());
            }
            return parameter;
        }

        RequestBody requestBody = methodParameter.getParameterAnnotation(RequestBody.class);
        if (requestBody != null) {
            parameter.setAnnotationType("RequestBody");
            if (!isBaseClass(clazz)) {
                parameter.setType("body");
                List<Class<?>> parents = new ArrayList<>();
                parents.add(clazz);
                parameter.setValue(formatClass(clazz, parents));
            }
            return parameter;
        }
        // 如果参数没有注解， 先判断是否是基本数据类型（Spring 没加注解默认会以RequestParam处理）
        if (isBaseClass(clazz)) {
            parameter.setAnnotationType("RequestParam");
            return parameter;
        }

        parameter.setAnnotationType("RequestParam");
        parameter.setType("body");
        List<Class<?>> parents = new ArrayList<>();
        parents.add(clazz);
        parameter.setValue(formatClass(clazz, parents));
        return parameter;
    }

    public String formatClass(Class<?> clazz0, List<Class<?>> parents) {
        StringBuilder result = new StringBuilder();
        result.append("{");
        Field[] fields = clazz0.getDeclaredFields();
        int index = 0;
        for (Field field : fields) {
            index++;
            Class<?> clazz = field.getType();
            if (clazz == int.class || clazz == Integer.class || clazz == long.class || clazz == Long.class
                    || clazz == double.class || clazz == Double.class || clazz == float.class || clazz == Float.class) {
                result.append("\"").append(field.getName()).append("\"").append(":").append(0);
            } else if (clazz == String.class) {
                result.append("\"").append(field.getName()).append("\"").append(":").append("\"string\"");
            } else if (clazz == boolean.class || clazz == Boolean.class) {
                result.append("\"").append(field.getName()).append("\"").append(":").append("false");
            } else if (clazz == java.util.Date.class || clazz == java.sql.Date.class) {
                result.append("\"").append(field.getName()).append("\"").append(":").append(new Date().getTime());
            } else if (clazz == List.class) {
                Type type = field.getGenericType();
                if (type instanceof ParameterizedType) {
                    ParameterizedType paramType = (ParameterizedType) type;
                    Type[] actualTypes = paramType.getActualTypeArguments();
                    result.append("\"").append(field.getName()).append("\"").append(":");
                    // TODO 获取泛型类型，设想只有一个，此处是个数组，有待观察
                    if (actualTypes.length == 0) {
                        result.append("[]");
                    } else {
                        result.append("[");
                        if (actualTypes[0] instanceof Class) {
                            Class<?> clz = (Class<?>) actualTypes[0];
                            String s = formatClass1(clz, parents);
                            result.append(s).append("]");
                        } else {
                            result.append("]");
                        }
                    }
                }
            } else {
                // 这里需要注意，类的嵌套可能会无限循环，所以引入parents来排除无限嵌套
                if (parents.contains(clazz)) {
                    result.append("\"").append(field.getName()).append("\"").append(":").append("{...}");
                } else {
                    parents.add(clazz);
                    result.append("\"").append(field.getName()).append("\"").append(":")
                            .append(formatClass(clazz, parents));
                }
            }
            if (fields.length != index) {
                result.append(",");
            }
        }
        result.append("}");
        return result.toString();
    }

    public String formatClass1(Class<?> clazz, List<Class<?>> parents) {
        StringBuilder result = new StringBuilder();
        if (clazz == int.class || clazz == Integer.class || clazz == long.class || clazz == Long.class
                || clazz == double.class || clazz == Double.class || clazz == float.class || clazz == Float.class) {
            result.append(0);
        } else if (clazz == String.class) {
            result.append("\"string\"");
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            result.append("false");
        } else if (clazz == java.util.Date.class || clazz == java.sql.Date.class) {
            result.append(new Date().getTime());
        } else {
            // 这里需要注意，类的嵌套可能会无限循环，所以引入parents来排除无限嵌套
            if (parents.contains(clazz)) {
                result.append("{...}");
            } else {
                parents.add(clazz);
                result.append(formatClass(clazz, parents));
            }
        }
        return result.toString();
    }

}
