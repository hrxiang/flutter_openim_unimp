#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint flutter_openim_unimp.podspec` to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'flutter_openim_unimp'
  s.version          = '0.0.1'
  s.summary          = 'A new Flutter plugin project.'
  s.description      = <<-DESC
A new Flutter plugin project.
                       DESC
  s.homepage         = 'http://example.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Your Company' => 'email@example.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/UniMPSDK/Core/Headers/*','Classes/*.{h,m}'
  s.public_header_files = 'Classes/UniMPSDK/Core/Headers/*','Classes/*.h'
  s.dependency 'Flutter'
  s.platform = :ios, '13.0'

  s.resource_bundles = {
    'Resources' => ['Classes/UniMPSDK/Core/Resources/*'],
  }

  s.vendored_libraries = 'Classes/**/*.a'
  s.vendored_frameworks = 'Classes/**/*.framework'
  s.frameworks = 'JavaScriptCore', 'CoreMedia', 'MediaPlayer', 'AVFoundation', 'AVKit', 'GLKit', 'OpenGLES', 'CoreText', 'QuartzCore', 'CoreGraphics', 'QuickLook', 'CoreTelephony', 'AssetsLibrary', 'CoreLocation', 'AddressBook'
  
  s.static_framework = true
  # Flutter.framework does not contain a i386 slice.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'i386' }
end
