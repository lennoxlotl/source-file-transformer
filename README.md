# source-file-transformer

As the project description suggests, a simple tool to change source file versions of class-files inside a jar to a given
version through ASM.

## Arguments

All arguments are required to use the source file transformer

```
--input, -i: The input file                    [String]
--output, -o: The output file                  [String]
--targetVersion, -t: The target source version [Int]
```

# Drawbacks

This tool may heavily mess with the validation of class files, it is only recommended to use this tool for tricking
compilers, not for running applications with another source version. (This whole project was developed for the sole
purpose of tricking the java compiler)
