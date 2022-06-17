import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class Cache {
    int cache_num;
    String total_cache_size;
    int associativity;
    int block_size;
    int hits = 0;
    double hit_rate;

    int num_tag_bits;
    int num_idx_bits;
    int block_offset;
    int num_ways;

    int[][] line_num_array;
    int[][] tag_array;


    public Cache(int cache_num,
                 int associativity,
                 int block_size,
                 int num_tag_bits,
                 int num_idx_bits,
                 int block_offset,
                 int num_ways) {
        this.cache_num = cache_num;
        if (cache_num == 7) {
            this.total_cache_size = "4096B";
        }
        else {
            this.total_cache_size = "2048B";
        }
        this.hits = 0;
        this.associativity = associativity;
        this.block_size = block_size;
        //make hit rate work
        this.hit_rate = this.hits / 5000000.0;
        this.num_tag_bits = num_tag_bits;
        this.num_idx_bits = num_idx_bits;
        this.block_offset = block_offset;
        this.num_ways = num_ways;
        if (num_ways > 1){
            this.line_num_array = new int[((int)Math.pow(2, num_idx_bits))][num_ways];
        }
        this.tag_array = new int [((int)Math.pow(2, num_idx_bits))][num_ways];
    }
}


public class lab6 {
    //instantiate each tag array
    //for each addr in file, grab tag bits and go to idx in array
    //make another array for timestamps for associative sets 3D array

    //parsing and hit and miss implementation

    public static void main(String[] args) throws FileNotFoundException {
        //2KB, direct mapped, 1-word blocks, 2^9 idx, 21 BITS tag
        Cache cache1 = new Cache(1, 1, 1, 21,9, 0, 1);
        //2KB, direct mapped, 2-word blocks, 2^8 idx, 21 BITS tag
        Cache cache2 = new Cache(2, 1, 2, 21,8, 1, 1);
        //2KB, direct mapped, 4-word blocks, 2^7 idx, 21 BITS tag
        Cache cache3 = new Cache(3, 1, 4, 21,7, 2, 1);
        //2KB, 2-way set associative, 1-word blocks, 2^8 idx, 22 BITS
        Cache cache4 = new Cache(4, 2, 1, 22,8, 0, 2);
        //2KB, 4-way set associative, 1-word blocks, 2^7 idx, 23 BITS
        Cache cache5 = new Cache(5, 4, 1, 23,7, 0, 4);
        //2KB, 4-way set associative, 4-word blocks, 2^5 idx, 23 BITS
        Cache cache6 = new Cache(6, 4, 4, 23,5, 2, 4);
        //4KB, direct mapped, 1-word blocks, 2^10 idx, 20 BITS
        Cache cache7 = new Cache(7, 1, 1, 20,10, 0, 1);

        Cache[] cache_arr = {cache1, cache2, cache3, cache4, cache5, cache6, cache7};
        //Cache[] cache_arr = {cache4, cache5, cache6};
        for (Cache cache : cache_arr) {
            run_cache(cache, args[0]);
        }

    }

    public static void run_cache(Cache cache, String filename) throws FileNotFoundException {
        String line;
        String[] arr;
        int addr;
        int tag;
        int idx;
        int line_num = 0;
        Scanner stream = new Scanner(new File(filename));

        while (stream.hasNextLine()){
            line = stream.nextLine();
            arr = line.split("\t");
            //pass addr to extract for tag bits
            addr = Integer.parseInt(arr[1], 16);
            tag = addr >>> 32-cache.num_tag_bits;
            idx = extract_idx(addr, cache);
            //pass tag to cache hit & miss function
            hit_miss(tag, idx, cache, line_num);
            line_num++;
        }
        results(cache);
    }

    //shift bits depending on cache attr
    public static int extract_idx(int addr, Cache cache){
        int bit_mask = 0xFFFFFFFF;
        addr >>>= (cache.block_offset + 2);
        bit_mask = bit_mask >>> (32-cache.num_idx_bits);
        return addr & bit_mask;
    }

    //print results for all caches
    public static void results(Cache cache){
        System.out.printf("Cache #%d\n", cache.cache_num);
        System.out.printf("Cache size: %s\tAssociativity: %s\tBlock size: %s\n", cache.total_cache_size, cache.associativity, cache.block_size);
        System.out.printf("Hits: %s\tHit Rate: %.2f", cache.hits, (cache.hits / 5000000.0) * 100);
        System.out.println("%");
        System.out.println("---------------------------");
    }

    public static int getIndexOfSmallest( int[] array )
    {
        int smallest = 0;
        for ( int i = 1; i < array.length; i++ )
        {
            if ( array[i] < array[smallest] ) smallest = i;
        }
        return smallest; // position of the first largest found
    }

    public static int check_empty_spots(int [] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 0) {
                return i;
            }
        }
        return -1;
    }

    public static void hit_miss(int tag, int idx, Cache cache, int curr_line_num){
        //issue with full array [5,3] and you want to look for 3, but i = 0
        int hit_flag = 0;
        int empty_idx = check_empty_spots(cache.tag_array[idx]);
        for (int i = 0; i < cache.tag_array[idx].length; i++) {
            if (cache.tag_array[idx][i] == tag) { //hit
                cache.hits++;
                hit_flag = 1;
                if (cache.num_ways > 1)
                    cache.line_num_array[idx][i] = curr_line_num; //update line number
                break;
            }
        }
        if ((empty_idx != -1) && (hit_flag == 0)){//miss - if there are empty spots
            cache.tag_array[idx][empty_idx] = tag;
            if (cache.num_ways > 1)
                cache.line_num_array[idx][empty_idx] = curr_line_num; //update line number
        }

        else if (hit_flag == 0){ //miss - there are no empty spots
            if (cache.num_ways == 1) {
                cache.tag_array[idx][0] = tag;
                return;
            }
            int lru = getIndexOfSmallest(cache.line_num_array[idx]);
            cache.tag_array[idx][lru] = tag;
            cache.line_num_array[idx][lru] = curr_line_num; //update line number
        }
    }

}
