# Make sure Qt6 is found before anything else.
set(Qt6Mqtt_FOUND FALSE)

if("${_qt_cmake_dir}" STREQUAL "")
    set(_qt_cmake_dir "${QT_TOOLCHAIN_RELOCATABLE_CMAKE_DIR}")
endif()
set(__qt_use_no_default_path_for_qt_packages "NO_DEFAULT_PATH")
if(QT_DISABLE_NO_DEFAULT_PATH_IN_QT_PACKAGES)
    set(__qt_use_no_default_path_for_qt_packages "")
endif()

# Don't propagate REQUIRED so we don't immediately FATAL_ERROR, rather let the find_dependency calls
# set _NOT_FOUND_MESSAGE which will be displayed by the includer of the Dependencies file.
set(${CMAKE_FIND_PACKAGE_NAME}_FIND_REQUIRED FALSE)

if(NOT Qt6_FOUND)
    find_dependency(Qt6 6.6.1
        PATHS
            "${CMAKE_CURRENT_LIST_DIR}/.."
            "${_qt_cmake_dir}"
            ${_qt_additional_packages_prefix_paths}
            ${QT_EXAMPLES_CMAKE_PREFIX_PATH}
        ${__qt_use_no_default_path_for_qt_packages}
    )
endif()


# note: _third_party_deps example: "ICU\\;FALSE\\;1.0\\;i18n uc data;ZLIB\\;FALSE\\;\\;"
set(__qt_Mqtt_third_party_deps "")
_qt_internal_find_third_party_dependencies("Mqtt" __qt_Mqtt_third_party_deps)

# Find Qt tool package.
set(__qt_Mqtt_tool_deps "")
_qt_internal_find_tool_dependencies("Mqtt" __qt_Mqtt_tool_deps)

# note: target_deps example: "Qt6Core\;5.12.0;Qt6Gui\;5.12.0"
set(__qt_Mqtt_target_deps "Qt6Core\;6.6.1;Qt6Network\;6.6.1")
set(__qt_Mqtt_find_dependency_paths "${CMAKE_CURRENT_LIST_DIR}/.." "${_qt_cmake_dir}")
_qt_internal_find_qt_dependencies("Mqtt" __qt_Mqtt_target_deps
                                  __qt_Mqtt_find_dependency_paths)

set(_Qt6Mqtt_MODULE_DEPENDENCIES "Core;Network")
set(Qt6Mqtt_FOUND TRUE)
