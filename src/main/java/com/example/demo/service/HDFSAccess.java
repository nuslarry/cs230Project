package com.example.demo.service;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

public class HDFSAccess {

    private static HDFSAccess single_instance=null;

    private FileSystem hdfs;

    private HDFSAccess() throws IOException {
        Configuration conf = new Configuration ();
        conf.set("fs.default.name", "hdfs://3.233.215.15:8020");
        hdfs = FileSystem.get(conf);
    }

    public static HDFSAccess getInstance() throws IOException {
        if (single_instance == null)
        {
            single_instance = new HDFSAccess();
        }

        single_instance.autoScale();
        return single_instance;
    }

    private void autoScale() throws IOException {
        FsStatus status = hdfs.getStatus();
        RemoteIterator<LocatedFileStatus> statuses = hdfs.listFiles(new Path("/"),true);
        while(statuses.hasNext()){
            LocatedFileStatus cur = statuses.next();
            cur.getPath();
        }
        hdfs.copyFromLocalFile(new Path("D:\\large_input.csv"),new Path("/"));
        double fraction = (double)status.getRemaining()/status.getCapacity();
        if(fraction<0.1){
            //TODO: scale up

        }
    }

    public boolean createDirIfNotExist(String dirPath){
        return false;
    }

    public boolean uploadFile(String srcPath,String dstPath,int replicationFactor){
        return false;
    }

    public InputStream readFile(String path){
       return null;
    }

    public boolean deleteFile(String path){
        return false;
    }


}
