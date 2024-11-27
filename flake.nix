{
  inputs.nixpkgs.url = "nixpkgs/nixpkgs-unstable";

  outputs = inputs: let
    system = "x86_64-linux";
    pkgs = inputs.nixpkgs.legacyPackages.${system};
  in {
    devShell.${system} = pkgs.mkShell rec {
      name = "java-shell";
      buildInputs = with pkgs; [
        (pkgs.jdk21.override {enableJavaFX = true;})
        maven
        glib
        libGL
        xorg.libX11
        xorg.libXxf86vm
        xorg.libXtst

        # Not for building, but a useful javaFX scene builder
        scenebuilder
        # Useful for debugging
        scenic-view
      ];
      LD_LIBRARY_PATH = pkgs.lib.makeLibraryPath buildInputs;
      shellHook = ''
        export JAVA_HOME=${pkgs.jdk21}
        PATH="${pkgs.jdk21}/bin:$PATH"
      '';
    };
  };
}
